package backend_for_react.backend_for_react.service;

import backend_for_react.backend_for_react.common.enums.Status;
import backend_for_react.backend_for_react.common.enums.VoucherStatus;
import backend_for_react.backend_for_react.common.enums.VoucherType;
import backend_for_react.backend_for_react.common.utils.SecurityUtils;
import backend_for_react.backend_for_react.controller.request.Voucher.VoucherCreationRequest;
import backend_for_react.backend_for_react.controller.request.Voucher.VoucherUpdateRequest;
import backend_for_react.backend_for_react.controller.response.PageResponse;
import backend_for_react.backend_for_react.controller.response.VoucherResponse;
import backend_for_react.backend_for_react.exception.BusinessException;
import backend_for_react.backend_for_react.exception.ErrorCode;
import backend_for_react.backend_for_react.exception.MessageError;
import backend_for_react.backend_for_react.mapper.VoucherMapper;
import backend_for_react.backend_for_react.model.User;
import backend_for_react.backend_for_react.model.UserRank;
import backend_for_react.backend_for_react.model.Voucher;
import backend_for_react.backend_for_react.repository.ProductRepository;
import backend_for_react.backend_for_react.repository.UserRankRepository;
import backend_for_react.backend_for_react.repository.VoucherRepository;
import backend_for_react.backend_for_react.repository.VoucherUsageRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j(topic = "VOUCHER-SERVICE")
@RequiredArgsConstructor
@Service
@FieldDefaults(makeFinal = true , level = AccessLevel.PRIVATE)
public class VoucherService {
    VoucherRepository voucherRepository;
    VoucherUsageRepository voucherUsageRepository;
    UserRankRepository userRankRepository;
    SecurityUtils securityUtils;

//    public PageResponse<VoucherResponse> findAll(String keyword, String sort, int page, int size){
//        log.info("Find all vouchers ");
//
//        Sort order = Sort.by(Sort.Direction.ASC, "id");
//        if (sort != null && !sort.isEmpty()) {
//            Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)"); //tencot:asc||desc
//            Matcher matcher = pattern.matcher(sort);
//            if (matcher.find()) {
//                String columnName = matcher.group(1);
//                if (matcher.group(3).equalsIgnoreCase("asc")) {
//                    order = Sort.by(Sort.Direction.ASC, columnName);
//                } else {
//                    order = Sort.by(Sort.Direction.DESC, columnName);
//                }
//            }
//        }
//        int pageNo = 0;
//        if (page > 0) {
//            pageNo = page - 1;
//        }
//        Pageable pageable = PageRequest.of(pageNo, size, order);
//        Page<Voucher> vouchers = null;
//        if (keyword == null || keyword.isEmpty()) {
//            vouchers = voucherRepository.findAllByStatus(VoucherStatus.ACTIVE,pageable);
//        } else {
//            keyword = "%" + keyword.toLowerCase() + "%";
//            vouchers = voucherRepository.searchByKeyword(keyword, pageable,VoucherStatus.ACTIVE);
//        }
//        PageResponse response = getVoucherPageResponse(pageNo, size, vouchers);
//        return response;
//    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('VIEW_ALL_VOUCHER')")
    public PageResponse<VoucherResponse> findAllByAdmin(String keyword, String rank, String sort, int page, int size){
        log.info("Find all vouchers ");

        Sort order = Sort.by(Sort.Direction.ASC, "id");
        if (sort != null && !sort.isEmpty()) {
            Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)"); //tencot:asc||desc
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
        Page<Voucher> vouchers = null;

        // Không có keyword & rank
        if ((keyword == null || keyword.isEmpty()) && (rank == null || rank.isEmpty())) {
            vouchers = voucherRepository.findAll(pageable);
        }
        // Có rank nhưng không có keyword
        else if (keyword == null || keyword.isEmpty()) {
            vouchers = voucherRepository.findByUserRank_NameAndUserRankStatus(rank, pageable,Status.ACTIVE);
        }
        // Có keyword nhưng không có rank
        else if (rank == null || rank.isEmpty()) {
            keyword = "%" + keyword.toLowerCase() + "%";
            vouchers = voucherRepository.searchByKeyword(keyword, pageable);
        }
        // Có cả keyword và rank
        else {
            keyword = "%" + keyword.toLowerCase() + "%";
            vouchers = voucherRepository.searchByKeywordAndRank(keyword, rank, pageable);
        }
        PageResponse response = getVoucherPageResponse(pageNo, size, vouchers);
        return response;
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('ADD_VOUCHER')")
    public void add(VoucherCreationRequest request) {
        log.info("request : {}" , request);
        Voucher voucher = new Voucher();
        voucher.setCode(request.getCode());
        voucher.setDiscription(request.getDescription());
        voucher.setType(request.getType());
        voucher.setDiscountValue(request.getDiscountValue());
        voucher.setMaxDiscountValue(request.getMaxDiscountValue());
        voucher.setMinDiscountValue(request.getMinDiscountValue());
        voucher.setTotalQuantity(request.getTotalQuantity());
        voucher.setStartDate(request.getStartDate());
        voucher.setEndDate(request.getEndDate());
        voucher.setRemainingQuantity(request.getTotalQuantity());
        voucher.setUsedQuantity(0);
        voucher.setIsShipping(request.getIsShipping());
        voucher.setStatus(VoucherStatus.ACTIVE);

        if(request.getUsageLimitPerUser() != null){
            voucher.setUsageLimitPerUser(request.getUsageLimitPerUser());
        }
        if(request.getUserRankId() != null){
            UserRank userRank = userRankRepository.findByIdAndStatus(request.getUserRankId(), Status.ACTIVE)
                            .orElseThrow(()-> new BusinessException(ErrorCode.BAD_REQUEST,"User rank not found"));
            voucher.setUserRank(userRank);
        }
        voucherRepository.save(voucher);
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('UPDATE_VOUCHER')")
    public void update(VoucherUpdateRequest request){
        Voucher voucher = voucherRepository.findByIdAndStatus(request.getId(),VoucherStatus.ACTIVE)
                .orElseThrow(()-> new BusinessException(ErrorCode.BAD_REQUEST , MessageError.VOUCHER_NOT_FOUND));
        if(request.getCode() != null){
            voucher.setCode(request.getCode());
        }
        if(request.getDiscription() != null){
            voucher.setDiscription(request.getDiscription());
        }
        if(request.getType() != null){
            voucher.setType(request.getType());
        }
        if(request.getDiscountValue() != null){
            voucher.setDiscountValue(request.getDiscountValue());
        }
        if(request.getMaxDiscountValue() != null){
            voucher.setMaxDiscountValue(request.getMaxDiscountValue());
        }
        if(request.getMinDiscountValue() != null){
            voucher.setMinDiscountValue(request.getMinDiscountValue());
        }
        if(request.getTotalQuantity() != null){
            voucher.setTotalQuantity(request.getTotalQuantity());
        }
        if(request.getStartDate() != null){
            voucher.setStartDate(request.getStartDate());
        }
        if(request.getEndDate() != null){
            voucher.setEndDate(request.getEndDate());
        }
        if(request.getStatus() != null){
            voucher.setStatus(request.getStatus());
        }

        if(request.getUsageLimitPerUser() != null){
            voucher.setUsageLimitPerUser(request.getUsageLimitPerUser());
        }
        if(request.getUserRankId() != null){
            UserRank userRank = userRankRepository.findByIdAndStatus(request.getUserRankId(),Status.ACTIVE)
                    .orElseThrow(()-> new BusinessException(ErrorCode.BAD_REQUEST,"User rank not found"));
            voucher.setUserRank(userRank);
        }
        voucherRepository.save(voucher);
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('DELETE_VOUCHER')")
    public void delete(Long id){
        Voucher voucher = voucherRepository.findByIdAndStatus(id,VoucherStatus.ACTIVE)
                .orElseThrow(()-> new BusinessException(ErrorCode.BAD_REQUEST, MessageError.VOUCHER_NOT_FOUND));
        voucher.setStatus(VoucherStatus.DISABLED);
    }

    public VoucherResponse getVoucherById(Long id){
        Voucher voucher = voucherRepository.findByIdAndStatus(id,VoucherStatus.ACTIVE)
                .orElseThrow(()-> new BusinessException(ErrorCode.BAD_REQUEST, MessageError.VOUCHER_NOT_FOUND));
        return VoucherMapper.toVoucherResponse(voucher);
    }

    public boolean validateVoucherWithOderAmount(Voucher voucher , BigDecimal orderAmount){
        LocalDateTime now = LocalDateTime.now();
        if(orderAmount.compareTo(BigDecimal.valueOf(voucher.getMinDiscountValue())) < 0){
            throw new BusinessException(ErrorCode.BAD_REQUEST,"Order is not eligible for voucher");
        }
        if(now.isBefore(voucher.getStartDate()) ||now.isAfter(voucher.getEndDate())){
            throw new BusinessException(ErrorCode.BAD_REQUEST,"Voucher is expired");
        }
        if(voucher.getRemainingQuantity() <= 0){
            throw new BusinessException(ErrorCode.BAD_REQUEST,  "Voucher is out of stock");
        }
        return true;
    }
    @Transactional(readOnly = true)
    public boolean validateVoucherUsageUser(Voucher voucher, User user) {
        log.info("voucher : {} user : {}", voucher.getId(), user != null ? user.getId() : null);

        // Nếu chưa đăng nhập
        if (user == null) {
            if (voucher.getUserRank() != null) {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "You must login to use this voucher");
            }
            return true; // Voucher không yêu cầu rank => cho phép
        }

        // Nếu voucher yêu cầu rank
        if (voucher.getUserRank() != null) {
            UserRank voucherRank = voucher.getUserRank();
            UserRank userRank = user.getUserRank();

            if (userRank == null) {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "You don't have a valid rank to use this voucher");
            }

            // So sánh cấp độ rank (level càng cao nghĩa là rank càng cao)
            if (userRank.getLevel() < voucherRank.getLevel()) {
                throw new BusinessException(ErrorCode.BAD_REQUEST,
                        String.format("Your rank '%s' cannot use voucher for '%s' members",
                                userRank.getName(), voucherRank.getName()));
            }
        }

        //Kiểm tra giới hạn sử dụng
        Integer countUsed = voucherUsageRepository.countByVoucherIdAndUserId(voucher.getId(), user.getId());
        int usedCount = countUsed == null ? 0 : countUsed;

        if (usedCount >= voucher.getUsageLimitPerUser()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "You have reached the maximum usage limit for this voucher");
        }

        return true;
    }

    public List<VoucherResponse> getAvailableVouchersForUser(){
        User user = securityUtils.getCurrentUser();
        LocalDateTime now = LocalDateTime.now();
        log.info("User rank : {} " , user.getUserRank());
        Integer userLevel = user.getUserRank().getLevel();
        List<Voucher> allValidVouchers = voucherRepository.findAllAvaiableForRank(now, userLevel);
        List<VoucherResponse> result = allValidVouchers.stream()
                .filter(voucher -> validateVoucherUsageUser(voucher,user))
                .map(VoucherMapper::toVoucherResponse)
                .toList();
        return result;
    }
    public BigDecimal calculateDiscountValue(BigDecimal orderAmount , Voucher voucher){
        BigDecimal discount;
        log.info("order amount : {} ", orderAmount);
        if(voucher.getType() == VoucherType.PERCENTAGE){
            BigDecimal percent = BigDecimal.valueOf(voucher.getDiscountValue()).divide(BigDecimal.valueOf(100));
            discount = orderAmount.multiply(percent);
            log.info("discount : {} ", discount);
        }else {
            discount = BigDecimal.valueOf(voucher.getDiscountValue());
        }
        if(voucher.getMaxDiscountValue() != null){
            discount = discount.min(BigDecimal.valueOf(voucher.getMaxDiscountValue()));
        }
        return discount;
    }
    public void decreaseVoucherQuantity(Voucher voucher) {
        voucher.setUsedQuantity(voucher.getUsedQuantity() + 1);
        voucher.setRemainingQuantity(voucher.getRemainingQuantity() - 1);
        voucherRepository.save(voucher);
    }
    private PageResponse<VoucherResponse> getVoucherPageResponse(int page, int size, Page<Voucher> vouchers) {
        List<VoucherResponse> voucherList = vouchers.stream()
                .map(voucher -> VoucherMapper.toVoucherResponse(voucher))
                .toList();

        PageResponse<VoucherResponse> response = new PageResponse<>();
        response.setPageNumber(page);
        response.setPageSize(size);
        response.setTotalElements(vouchers.getTotalElements());
        response.setTotalPages(vouchers.getTotalPages());
        response.setData(voucherList);
        return response;
    }

}
