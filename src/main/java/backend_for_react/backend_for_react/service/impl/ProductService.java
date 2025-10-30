package backend_for_react.backend_for_react.service.impl;

import backend_for_react.backend_for_react.common.enums.ProductStatus;
import backend_for_react.backend_for_react.common.enums.Status;
import backend_for_react.backend_for_react.common.utils.CloudinaryHelper;
import backend_for_react.backend_for_react.controller.request.Attribute.AttributeCreationRequest;
import backend_for_react.backend_for_react.controller.request.Product.ProductCreationRequest;
import backend_for_react.backend_for_react.controller.request.Product.ProductUpdateRequest;
import backend_for_react.backend_for_react.controller.request.ProductVariant.ProductVariantCreationRequest;
import backend_for_react.backend_for_react.controller.request.VariantAttribute.VariantAttributeRequest;
import backend_for_react.backend_for_react.controller.request.VariantQuantityUpdateRequest;
import backend_for_react.backend_for_react.controller.response.*;
import backend_for_react.backend_for_react.exception.BusinessException;
import backend_for_react.backend_for_react.exception.ErrorCode;
import backend_for_react.backend_for_react.model.*;
import backend_for_react.backend_for_react.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j(topic = "PRODUCT - SERVCIE")
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ImageProductRepository imageProductRepository;
    private final AttributeRepository attributeRepository;
    private final ProductVariantRepository productVariantRepository;
    private final AttributeValueRepository attributeValueRepository;
    private final VariantAttributeValueRepository variantAttributeValueRepository;
    private final ImageProductDescriptionRepository imageProductDescriptionRepository;


    public PageResponse<ProductBaseResponse> findAll(String keyword, String sort, int page, int size) {
        log.info("KEYWORD : ", keyword);
        Sort order = Sort.by(Sort.Direction.ASC, "id");
        if (sort != null && !sort.isEmpty()) {
            Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
            Matcher matcher = pattern.matcher(sort);
            if (matcher.find()) {
                String columnName = matcher.group(1);
                if (matcher.group(3).equalsIgnoreCase("asc")) {
                    order = Sort.by(Sort.Direction.ASC, columnName);
                } else {
                    order = Sort.by(Sort.Direction.DESC, columnName);
                }
            }
        }
        int pageNo = 0;
        if (page > 0) {
            pageNo = page - 1;
        }
        Pageable pageable = PageRequest.of(pageNo, size, order);
        Page<Product> products = null;
        if (keyword == null || keyword.isEmpty()) {
            products = productRepository.findAllByProductStatus(ProductStatus.ACTIVE, pageable);
        } else {
            log.info("Keyword");
            keyword = "%" + keyword.toLowerCase() + "%";
            products = productRepository.searchByKeyword(keyword,ProductStatus.ACTIVE, pageable);
        }
        PageResponse response = getProductPageResponse(pageNo, size, products);
        return response;
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('VIEW_ALL_PRODUCT')")
    public PageResponse<ProductBaseResponse> findAllByAdmin(String keyword, String sort, int page, int size) {
        log.info("KEYWORD : ", keyword);
        Sort order = Sort.by(Sort.Direction.ASC, "id");
        if (sort != null && !sort.isEmpty()) {
            Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
            Matcher matcher = pattern.matcher(sort);
            if (matcher.find()) {
                String columnName = matcher.group(1);
                if (matcher.group(3).equalsIgnoreCase("asc")) {
                    order = Sort.by(Sort.Direction.ASC, columnName);
                } else {
                    order = Sort.by(Sort.Direction.DESC, columnName);
                }
            }
        }
        int pageNo = 0;
        if (page > 0) {
            pageNo = page - 1;
        }
        Pageable pageable = PageRequest.of(pageNo, size, order);
        Page<Product> products = null;
        if (keyword == null || keyword.isEmpty()) {
            products = productRepository.findAll(pageable);
        } else {
            log.info("Keyword");
            keyword = "%" + keyword.toLowerCase() + "%";
            products = productRepository.searchByKeyword(keyword,pageable);
        }
        PageResponse response = getProductPageResponse(pageNo, size, products);
        return response;
    }


    public PageResponse<ProductBaseResponse> findAllByCategory(
            Long categoryId, String keyword, String sort, int page, int size) {

        // B1. Lấy danh sách categoryId bao gồm cả cha và con
        List<Long> categoryIds = new ArrayList<>();
        collectCategoryIds(categoryId, categoryIds);

        // B2. Xử lý sắp xếp
        Sort order = Sort.by(Sort.Direction.ASC, "id");
        if (sort != null && !sort.isEmpty()) {
            Matcher matcher = Pattern.compile("(\\w+?)(:)(.*)").matcher(sort);
            if (matcher.find()) {
                order = Sort.by(
                        matcher.group(3).equalsIgnoreCase("asc")
                                ? Sort.Direction.ASC
                                : Sort.Direction.DESC,
                        matcher.group(1)
                );
            }
        }

        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size, order);

        // B3. Gọi repository
        Page<Product> products;
        if (keyword == null || keyword.isEmpty()) {
            products = productRepository.findByCategoryIds(categoryIds, ProductStatus.ACTIVE, pageable);
        } else {
            keyword = "%" + keyword.toLowerCase() + "%";
            products = productRepository.searchByKeywordAndCategory(keyword, ProductStatus.ACTIVE, categoryIds, pageable);
        }

        // B4. Map ra response
        return getProductPageResponse(page, size, products);
    }

    /** Hàm đệ quy lấy tất cả category con **/
    private void collectCategoryIds(Long parentId, List<Long> ids) {
        ids.add(parentId);
        List<Category> children = categoryRepository.findByParent_Id(parentId);
        for (Category child : children) {
            collectCategoryIds(child.getId(), ids);
        }
    }


    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('ADD_PRODUCT')")
    public void save(ProductCreationRequest req) throws IOException {
        log.info("REQUEST : ", req);
        List<String> uploadedUrls = new ArrayList<>();

        // 1. Tạo sản phẩm chính
        Product product = createBaseProduct(req);

        // 2. Xử lý attributes (nếu có)
        List<Attribute> attributes = new ArrayList<>();
        if (req.getAttributes() != null && !req.getAttributes().isEmpty()) {
            attributes = processAttributes(product, req.getAttributes());
        }

        // 3. Xử lý variants (nếu có). Nếu không có, tạo biến thể mặc định.
        if (req.getProductVariant() != null && !req.getProductVariant().isEmpty()) {
            processVariants(product, attributes, req.getProductVariant());
        } else {
            // === QUAN TRỌNG: Tạo biến thể mặc định nếu sản phẩm không có variant ===
            createDefaultVariantForProduct(product);
        }
    }


    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('UPDATE_PRODUCT')")
    public void update(ProductUpdateRequest req) {
        Product product = productRepository.findById(req.getId()).orElseThrow(() -> new EntityNotFoundException("Product not found"));
        if (req.getCategoryId() != null) {
            Category category = categoryRepository.findById(req.getCategoryId()).orElseThrow(() -> new EntityNotFoundException("Category not found"));
            product.setCategory(category);
        }
        if (req.getDescription() != null) product.setDescription(req.getDescription());
        if (req.getVideo() != null) product.setUrlvideo(req.getVideo());
        if (req.getCoverImage() != null) product.setUrlCoverImage(req.getCoverImage());
        productRepository.save(product);
    }

    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('DELETE_PRODUCT')")
    public void delete(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Product not found"));
        product.setProductStatus(ProductStatus.INACTIVE);
        productRepository.save(product);
    }


    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Product not found"));
        return getProductDetailResponse(product);
    }

    /**
     * Tao san pham chung
     */
    private Product createBaseProduct(ProductCreationRequest req) {
        log.info("REQ: ", req);
        // Tìm danh mục
        Category category = categoryRepository.findById(req.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy danh mục"));

        // Tạo sản phẩm chính
        Product newProduct = Product.builder()
                .category(category)
                .soldQuantity(0)
                .avgRating(0.0)
                .description(req.getDescription())
                .name(req.getName())
                .listPrice(req.getListPrice())
                .salePrice(req.getSalePrice())
                .productStatus(ProductStatus.ACTIVE)
                .urlvideo(req.getVideo()) // Lấy từ URL đã upload trước
                .urlCoverImage(req.getCoverImage()) // Lấy từ URL đã upload trước
                .build();
        // Xử lý ảnh sản phẩm (nếu có)
        if (req.getImageProduct() != null) {
            req.getImageProduct().forEach(url -> {
                ImageProduct imageProduct = new ImageProduct();
                imageProduct.setProduct(newProduct);
                imageProduct.setUrl(url);
                imageProduct.setStatus(Status.ACTIVE);
                imageProductRepository.save(imageProduct);
            });
        }
        productRepository.save(newProduct);
        return newProduct;
    }

    /**
     * Xử lý tạo các phân loại (attributes) và giá trị của chúng
     */
    private Attribute processSingleAttribute(Product product, AttributeCreationRequest attributeRequest) {
        Attribute attribute = attributeRepository.findByName(attributeRequest.getName());
        if (attribute == null) {
            attribute = Attribute.builder()
                    .product(product)
                    .name(attributeRequest.getName())
                    .build();
            attributeRepository.save(attribute); // Gán attribute từ newAttribute
        }
        return attribute;
    }

    private List<Attribute> processAttributes(Product product, List<AttributeCreationRequest> req) {

        return req.stream().map(attributeRequest -> {
            Attribute attribute = processSingleAttribute(product, attributeRequest);
            List<AttributeValue> attributeValues = attributeRequest.getAttributeValue().stream()
                    .map(attributeValueRequest -> {
                        AttributeValue attributeValue = AttributeValue.builder()
                                .attribute(attribute)
                                .value(attributeValueRequest.getValue())
                                .status(Status.ACTIVE)
                                .urlImage(attributeValueRequest.getImage())
                                .build();
                        return attributeValueRepository.save(attributeValue);
                    }).toList();
            attribute.setValues(attributeValues);
            return attribute;
        }).toList();
    }

    /**
     * Tạo các biến thể sản phẩm từ các tổ hợp thuộc tính
     */
    private List<ProductVariant> processVariants(Product product, List<Attribute> attributes, List<ProductVariantCreationRequest> req) {
        log.info("ProductVariantCreationRequest: ", req);
        return req.stream().map(variantRequest -> {
            ProductVariant productVariant = ProductVariant.builder()
                    .product(product)
                    .height(variantRequest.getHeight())
                    .width(variantRequest.getWidth())
                    .length(variantRequest.getLength())
                    .weight(variantRequest.getWeight())
                    .price(variantRequest.getPrice())
                    .quantity(variantRequest.getQuantity())
                    .status(Status.ACTIVE)
                    .sku(generateSku(product, variantRequest))
                    .build();
            productVariantRepository.save(productVariant);
            variantRequest.getVariantAttributes().forEach(combo -> {
                AttributeValue attributeValue = findAttributeValue(attributes, combo).orElseThrow(() -> new BusinessException(
                        ErrorCode.EXISTED,
                        "Attribute value not found: " + combo.getAttribute() + " - " + combo.getValue()));
                VariantAttributeValue vav = VariantAttributeValue.builder()
                        .productVariant(productVariant)
                        .attributeValue(attributeValue)
                        .build();
                variantAttributeValueRepository.save(vav);
            });
            return productVariant;
        }).toList();
    }

    /**
     * Tạo một biến thể mặc định cho sản phẩm (dành cho sản phẩm đơn giản không có phân loại)
     */
    private ProductVariant createDefaultVariantForProduct(Product product) {
        // Tạo SKU mặc định dựa trên tên sản phẩm
        String defaultSku = generateDefaultSku(product);

        ProductVariant defaultVariant = ProductVariant.builder()
                .product(product)
                .price(product.getListPrice()) // Dùng luôn giá listPrice từ sản phẩm chính
                .quantity(0) // Khởi tạo số lượng = 0
                .sku(defaultSku)
                .status(Status.ACTIVE)
                .build();

        return productVariantRepository.save(defaultVariant);
    }

    /**
     * Hàm tạo SKU mặc định cho sản phẩm không biến thể
     */
    private String generateDefaultSku(Product product) {
        String productCode = product.getName().substring(0, Math.min(3, product.getName().length())).toUpperCase();
        String randomDigits = RandomStringUtils.randomNumeric(4);
        return productCode + "-DEF-" + randomDigits; // Ví dụ: "ABC-DEF-1234"
    }

    /**
     * Tìm giá trị thuộc tính theo tên và giá trị
     */
    private Optional<AttributeValue> findAttributeValue(List<Attribute> attributes, VariantAttributeRequest combo) {
        return attributes.stream()
                .filter(attr -> attr.getName().equalsIgnoreCase(combo.getAttribute()))
                .flatMap(attr -> attr.getValues().stream())
                .filter(value -> value.getValue().equalsIgnoreCase(combo.getValue()))
                .findFirst();
    }

    /**
     * Tạo SKU tự động
     */
    private String generateSku(Product product, ProductVariantCreationRequest request) {
        log.info("ProductVariantCreationRequest : ", request);
        String productCode = product.getName().substring(0, 3).toUpperCase();
        String variantCode = request.getVariantAttributes().stream()
                .sorted(Comparator.comparing(VariantAttributeRequest::getAttribute))
                .map(c -> c.getValue().substring(0, 1))
                .collect(Collectors.joining());
        return productCode + "-" + variantCode + "-" + RandomStringUtils.randomNumeric(4);
    }

    /**
     * Update quantity
     */
    @Transactional
    public void updateVariantQuantity(List<VariantQuantityUpdateRequest> req, Long productId) {
        for (VariantQuantityUpdateRequest updateRequest : req) {
            // 1. Tìm product
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new EntityNotFoundException("Product not found"));

            // 2. Tìm tất cả biến thể của product
            List<ProductVariant> variants = productVariantRepository.findAllByProduct(product);

            // 3. Tìm biến thể khớp với thuộc tính đầu vào
            ProductVariant targetVariant = variants.stream()
                    .filter(variant -> matchesAttributes(variant, updateRequest.getVariantAttributes()))
                    .findFirst()
                    .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXISTED, "Variant not found"));

            // 4. Cập nhật số lượng
            targetVariant.setQuantity(targetVariant.getQuantity() + updateRequest.getQuantityChange());
            productVariantRepository.save(targetVariant);
        }
    }

    // Helper: Kiểm tra biến thể có khớp với thuộc tính không
    private boolean matchesAttributes(ProductVariant variant, List<VariantAttributeRequest> attributes) {
        List<VariantAttributeValue> variantAttributes = variantAttributeValueRepository.findAllByProductVariant(variant);

        return attributes.stream().allMatch(inputAttr ->
                variantAttributes.stream().anyMatch(variantAttr ->
                        variantAttr.getAttributeValue().getAttribute().getName().equals(inputAttr.getAttribute()) &&
                                variantAttr.getAttributeValue().getValue().equals(inputAttr.getValue())
                )
        );
    }

    /**
     * Xây dựng response
     */

    private ProductResponse getProductDetailResponse(Product product) {
        List<ProductVariantResponse> productVariantResponses = product.getVariants().stream().map(
            this::getProductVariantResponse
        ).toList();
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .salePrice(product.getSalePrice())
                .description(product.getDescription())
                .listPrice(product.getListPrice())
                .categoryId(product.getCategory().getId())
                .coverImage(product.getUrlCoverImage())
                .video(product.getUrlvideo())
                .soldQuantity(product.getSoldQuantity())
                .avgRating(product.getAvgRating())
                .imageProduct(getImageProduct(product))
                .attributes(getAttributeResponse(product))
                .productVariant(productVariantResponses)
                .productStatus(product.getProductStatus())
                .createAt(product.getCreatedAt())
                .updateAt(product.getUpdatedAt())
                .build();
    }

    private ProductBaseResponse getProductBaseResponse(Product product) {
        return ProductBaseResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .listPrice(product.getListPrice())
                .urlvideo(product.getUrlvideo())
                .urlCoverImage(product.getUrlCoverImage())
                .soldQuantity(product.getSoldQuantity())
                .description(product.getDescription())
                .avgRating(product.getAvgRating())
                .salePrice(product.getSalePrice())
                .status(product.getProductStatus())
                .createdAt(product.getCreatedAt())
                .updateAt(product.getUpdatedAt())
                .build();
    }

    public ProductVariantResponse getProductVariantResponse(ProductVariant productVariant) {
        return ProductVariantResponse.builder()
                .id(productVariant.getId())
                .price(productVariant.getPrice())
                .quantity(productVariant.getQuantity())
                .height(productVariant.getHeight())
                .width(productVariant.getWidth())
                .length(productVariant.getLength())
                .weight(productVariant.getWeight())
                .sku(productVariant.getSku())
                .variantAttributes(getVariantAttributeResponse(productVariant))
                .build();
    }


    private List<AttributeResponse> getAttributeResponse(Product product) {
        List<AttributeResponse> attributeResponses = attributeRepository.findAllByProduct(product).stream().map(
                attribute -> AttributeResponse.builder()
                        .id(attribute.getId())
                        .name(attribute.getName())
                        .attributeValue(getAttributeValueResponse(attribute))
                        .build()
        ).toList();
        return attributeResponses;
    }

    private List<AttributeValueResponse> getAttributeValueResponse(Attribute attribute) {
        List<AttributeValueResponse> attributeValueResponses = null;
        attributeValueResponses = attributeValueRepository.findAllByAttribute(attribute).stream().map(
                attributeValue -> AttributeValueResponse.builder()
                        .id(attributeValue.getId())
                        .image(attributeValue.getUrlImage())
                        .value(attributeValue.getValue())
                        .build()
        ).toList();

        return attributeValueResponses;
    }

    private List<VariantAttributeResponse> getVariantAttributeResponse(ProductVariant productVariant) {

        List<VariantAttributeResponse> variantAttributeResponses = variantAttributeValueRepository.findAllByProductVariant(productVariant).stream().map(

                variantAttributeValue -> {
                    Attribute attribute = attributeRepository.findById(variantAttributeValue.getAttributeValue().getAttribute().getId())
                            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXISTED, "Attribute not exist!"));
                    return VariantAttributeResponse.builder()
                            .id(variantAttributeValue.getAttributeValue().getId())
                            .attribute(attribute.getName())
                            .value(variantAttributeValue.getAttributeValue().getValue())
                            .build();
                }
        ).toList();
        return variantAttributeResponses;
    }

    private List<String> getImageProduct(Product product) {
        List<String> imageProduct = imageProductRepository.findAllByStatusAndProduct(Status.ACTIVE, product).stream().map(
                image -> image.getUrl()
        ).toList();
        return imageProduct;
    }

    private PageResponse<ProductBaseResponse> getProductPageResponse(int page, int size, Page<Product> products) {
        log.info("products : {}" ,products);
        List<ProductBaseResponse> productList = products.stream()
                .map(product -> getProductBaseResponse(product))
                .toList();
        PageResponse<ProductBaseResponse> response = new PageResponse<>();
        response.setPageNumber(page + 1);
        response.setPageSize(size);
        response.setTotalElements(products.getTotalElements());
        response.setTotalPages(products.getTotalPages());
        response.setData(productList);
        return response;
    }

}
