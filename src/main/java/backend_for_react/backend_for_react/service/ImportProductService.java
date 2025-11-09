package backend_for_react.backend_for_react.service;

import backend_for_react.backend_for_react.common.enums.DeliveryStatus;
import backend_for_react.backend_for_react.common.enums.Status;
import backend_for_react.backend_for_react.controller.request.ImportProduct.ImportProductCreationRequest;
import backend_for_react.backend_for_react.controller.request.ImportProductDetail.ImportDetailCreationRequest;
import backend_for_react.backend_for_react.controller.request.ImportProductDetail.UpdateImportDetailRequest;
import backend_for_react.backend_for_react.controller.response.*;
import backend_for_react.backend_for_react.exception.BusinessException;
import backend_for_react.backend_for_react.exception.ErrorCode;
import backend_for_react.backend_for_react.mapper.ProductVariantMapper;
import backend_for_react.backend_for_react.model.*;
import backend_for_react.backend_for_react.repository.ImportDetailRepository;
import backend_for_react.backend_for_react.repository.ImportProductRepository;
import backend_for_react.backend_for_react.repository.ProductVariantRepository;
import backend_for_react.backend_for_react.repository.SupplierRepository;
import backend_for_react.backend_for_react.service.impl.ProductService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Join;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j(topic = "SUPPLIER-SERVICE")
@RequiredArgsConstructor
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ImportProductService {
    ImportProductRepository importProductRepository;
    SupplierRepository supplierRepository;
    ProductVariantRepository productVariantRepository;
    ImportDetailRepository importDetailRepository;
    SupplierService supplierService;
    ProductService productServiceImpl;

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('VIEW_ALL_IMPORT_PRODUCT')")
    public PageResponse<ImportProductResponse> findAll(
            String keyword, String sort, int page, int size,
            String timeRange, LocalDateTime startDate, LocalDateTime endDate,
            Long supplierId, DeliveryStatus deliveryStatus) {

        log.info("---Find All--");

        // ===== Sort =====
        Sort order = Sort.by(Sort.Direction.ASC, "id");
        if (sort != null && !sort.isEmpty()) {
            Matcher matcher = Pattern.compile("(\\w+?)(:)(.*)").matcher(sort);
            if (matcher.find()) {
                String columnName = matcher.group(1);
                Sort.Direction direction = matcher.group(3).equalsIgnoreCase("asc")
                        ? Sort.Direction.ASC : Sort.Direction.DESC;
                order = Sort.by(direction, columnName);
            }
        }
        int pageNo = page > 0 ? page - 1 : 0;
        Pageable pageable = PageRequest.of(pageNo, size, order);

        // ===== TimeRange =====
        LocalDateTime calcStart = startDate;
        LocalDateTime calcEnd = endDate;

        if (timeRange != null) {
            LocalDate now = LocalDate.now();
            switch (timeRange.toUpperCase()) {
                case "TODAY":
                    calcStart = now.atStartOfDay();
                    calcEnd = now.plusDays(1).atStartOfDay();
                    break;
                case "YESTERDAY":
                    calcStart = now.minusDays(1).atStartOfDay();
                    calcEnd = now.atStartOfDay();
                    break;
                case "LASTWEEK":
                    calcStart = now.minusWeeks(1).with(DayOfWeek.MONDAY).atStartOfDay();
                    calcEnd = calcStart.plusDays(6).with(LocalTime.MAX);
                    break;
                case "LASTMONTH":
                    calcStart = now.minusMonths(1).withDayOfMonth(1).atStartOfDay();
                    calcEnd = calcStart.plusMonths(1).minusDays(1).with(LocalTime.MAX);
                    break;
                case "QUARTER":
                    calcStart = now.minusMonths(3).atStartOfDay();
                    calcEnd = now.atStartOfDay();
                    break;
            }
        }

        if (calcStart == null) calcStart = LocalDateTime.MIN;
        if (calcEnd == null) calcEnd = LocalDateTime.now();

        // tạo biến final để dùng trong lambda
        final LocalDateTime finalStart = calcStart;
        final LocalDateTime finalEnd = calcEnd;
        final String finalKeyword = (keyword != null && !keyword.isEmpty())
                ? "%" + keyword.toLowerCase() + "%"
                : null;

        // ===== Specification =====
        Specification<ImportProduct> spec = Specification.allOf(
                supplierId != null ? (root, query, cb) -> cb.equal(root.get("supplier").get("id"), supplierId) : null,
                deliveryStatus != null ? (root, query, cb) -> cb.equal(root.get("status"), deliveryStatus) : null,
                (root, query, cb) -> cb.between(root.get("createdAt"), finalStart, finalEnd),
                finalKeyword != null ? (root, query, cb) -> {
                    Join<?, ?> details = root.join("importDetails");
                    Join<?, ?> variant = details.join("productVariant");
                    Join<?, ?> product = variant.join("product");
                    return cb.or(
                            cb.like(cb.lower(variant.get("sku")), finalKeyword),
                            cb.like(cb.lower(product.get("name")), finalKeyword)
                    );
                } : null
        );

        Page<ImportProduct> pageData = importProductRepository.findAll(spec, pageable);
        return getImportProductPageResponse(pageNo, size, pageData);
    }


    @Transactional
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('ADD_IMPORT_PRODUCT')")
    public ImportProductResponse save (ImportProductCreationRequest request){
        Supplier supplier = supplierRepository.findByIdAndStatus(request.getSupplierId(),Status.ACTIVE).orElseThrow(()-> new EntityNotFoundException("Supplier not found"));

        ImportProduct importProduct = new ImportProduct();
        importProduct.setSupplier(supplier);
        importProduct.setDescription(request.getDescription());
        importProduct.setStatus(DeliveryStatus.PENDING);
        importProduct.setImportCode(generateImportCode());

        List<ImportDetail> importDetails = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for(ImportDetailCreationRequest detailReq : request.getImportDetails()){
            ProductVariant productVariant = productVariantRepository.findByIdAndStatus(detailReq.getProductVariantId(),Status.ACTIVE)
                    .orElseThrow(()-> new EntityNotFoundException("ProductVariant not found"));

            ImportDetail importDetail = new ImportDetail();
            importDetail.setImportProduct(importProduct);
            importDetail.setProductVariant(productVariant);
            importDetail.setStatus(Status.ACTIVE);
            importDetail.setQuantity(detailReq.getQuantity());
            importDetail.setUnitPrice(detailReq.getUnitPrice());

            importDetails.add(importDetail);

            BigDecimal detailTotal = importDetail.getTotalPrice();
            totalAmount = totalAmount.add(detailTotal);
        }
        importProduct.setImportDetails(importDetails);
        importProduct.setTotalAmount(totalAmount);

       importProductRepository.save(importProduct);
       return getImportProductResponse(importProduct);
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('VIEW_DETAIL_IMPORT_PRODUCT')")
    public ImportProductResponse getImportProductById(Long importProductId){
        ImportProduct importProduct = importProductRepository.findById(importProductId)
                .orElseThrow(()-> new EntityNotFoundException("Import not found"));
        return getImportProductResponse(importProduct);
    }

    private void recalculateImportTotalAmount(ImportProduct importProduct) {
        BigDecimal newTotalAmount = importProduct.getImportDetails().stream()
                .filter(detail -> detail.getStatus() == Status.ACTIVE) // CHỈ tính những detail còn ACTIVE
                .map(ImportDetail::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        importProduct.setTotalAmount(newTotalAmount);
        importProductRepository.save(importProduct);
    }

    /***
     * Cập nhật kho
     */
    @Transactional
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('CONFIRM_IMPORT_PRODUCT')")
    public void confirmImport(Long importId){
        // TÌM VÀ KIỂM TRA TRẠNG THÁI

        ImportProduct importProduct = importProductRepository.findById(importId)
                .orElseThrow(()-> new BusinessException(ErrorCode.BAD_REQUEST,"Import not found"));

        if(!importProduct.getStatus().equals(DeliveryStatus.PENDING)){
            throw new BusinessException(ErrorCode.BAD_REQUEST,"Import product not in PENDING");}
        // DUYỆT QUA TỪNG CHI TIẾT VÀ CẬP NHẬT SỐ LƯỢNG TỒN KHO
        importProduct.getImportDetails().stream()
                .filter(detail -> detail.getStatus() == Status.ACTIVE)
                .forEach(detail -> {
                    ProductVariant variant = detail.getProductVariant();
                    variant.setQuantity(variant.getQuantity() + detail.getQuantity());
                    productVariantRepository.save(variant);
                });
        // CHUYỂN TRẠNG THÁI
        importProduct.setStatus(DeliveryStatus.COMPLETED);
        importProductRepository.save(importProduct);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('CANCEL_IMPORT_PRODUCT')")
    public void cancelImport(Long importId) {
        ImportProduct importProduct = importProductRepository.findById(importId)
                .orElseThrow(() -> new EntityNotFoundException("Import not found"));

        // Chỉ hủy được phiếu ở trạng thái DRAFT
        if (importProduct.getStatus() != DeliveryStatus.PENDING) {
            throw new BusinessException(ErrorCode.BAD_REQUEST,"You just cancel import product with status PENDING");
        }

        importProduct.setStatus(DeliveryStatus.CANCELLED);
        importProductRepository.save(importProduct);
    }

    /***
     * Xoa detail import
     *
     */
    @Transactional
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('DELETE_IMPORT_PRODUCT_DETAIL')")
    public void removeDetailFromPendingImport(Long importId, Long detailId) {
        // 1. Tìm phiếu nhập và kiểm tra trạng thái DRAFT
        ImportProduct importProduct = importProductRepository.findById(importId)
                .orElseThrow(() -> new EntityNotFoundException("Import not found"));

        if (importProduct.getStatus() != DeliveryStatus.PENDING) {
            throw new BusinessException(ErrorCode.BAD_REQUEST,"You just cancel import product with status PENDING");
        }

        // 2. Tìm chi tiết cần xóa
        ImportDetail detailToRemove = importDetailRepository.findById(detailId)
                .orElseThrow(() -> new EntityNotFoundException("Import detail not found"));

        // 3. Kiểm tra chi tiết có thuộc về phiếu nhập này không
        if (!detailToRemove.getImportProduct().getId().equals(importId)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST,"Import detail not part of import");
        }

        // 4. THỰC HIỆN XÓA MỀM: Đổi trạng thái thành INACTIVE
        detailToRemove.setStatus(Status.INACTIVE);
        importDetailRepository.save(detailToRemove);

        // 5. (Optional) Recalculate total amount for the import
        recalculateImportTotalAmount(importProduct);
    }

    /***
     * Chinh sua so luong
     *
     */
    @Transactional
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('UPDATE_QUANTITY_IMPORT_PRODUCT')")
    public void updateQuantityDetailFromPendingImport (List<UpdateImportDetailRequest> request , Long  importId) {
        ImportProduct importProduct = importProductRepository.findById(importId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST,"Import not found"));
        if(importProduct.getStatus() != DeliveryStatus.PENDING) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Import not in PENDING");
        }
        for (UpdateImportDetailRequest detail : request) {
            ImportDetail changeDetail = importDetailRepository.findById(detail.getImportDetailId())
                    .orElseThrow(() -> new EntityNotFoundException("Import detail not found"));
            if (!changeDetail.getImportProduct().getId().equals(importId)) {
                throw new BusinessException(ErrorCode.BAD_REQUEST,"Import detail not part of this import");
            }
            changeDetail.setQuantity(detail.getQuantity());
            importDetailRepository.save(changeDetail);
        }
        recalculateImportTotalAmount(importProduct);
    }
    private PageResponse<ImportProductResponse> getImportProductPageResponse(int page, int size, Page<ImportProduct> importProducts){
        List<ImportProductResponse> userList = importProducts.stream()
                .map(this::getImportProductResponse)
                .toList();

        PageResponse<ImportProductResponse> response = new PageResponse<>();
        response.setPageNumber(page);
        response.setPageSize(size);
        response.setTotalElements(importProducts.getTotalElements());
        response.setTotalPages(importProducts.getTotalPages());
        response.setData(userList);
        return response;
    }
    private ImportDetailResponse getImportDetailResponse(ImportDetail importDetail) {
        ProductVariantResponse productVariantResponse = ProductVariantMapper.getProductVariantResponse(importDetail.getProductVariant());
        return ImportDetailResponse.builder()
                .id(importDetail.getId())
                .unitPrice(importDetail.getUnitPrice())
                .quantity(importDetail.getQuantity())
                .totalPrice(importDetail.getTotalPrice())
                .productVariantResponse(productVariantResponse)
                .build();
    }
    private ImportProductResponse getImportProductResponse(ImportProduct importProduct){
        SupplierResponse supplierResponse = supplierService.getSupplierResponse(importProduct.getSupplier());
        List<ImportDetailResponse> importProductResponses = importProduct.getImportDetails().stream()
                .filter(importDetail -> importDetail.getStatus() == Status.ACTIVE)
                .map(this::getImportDetailResponse).toList();
        return ImportProductResponse.builder()
                .id(importProduct.getId())
                .importCode(importProduct.getImportCode())
                .description(importProduct.getDescription())
                .totalAmount(importProduct.getTotalAmount())
                .status(importProduct.getStatus())
                .supplierResponse(supplierResponse)
                .importDetailResponses(importProductResponses)
                .build();
    }
    private String generateImportCode() {
        Long count = importProductRepository.count() + 1;
        return "PN-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) +
                "-" + String.format("%04d", count);
    }
}
