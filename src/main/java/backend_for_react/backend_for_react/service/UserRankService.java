package backend_for_react.backend_for_react.service;

import backend_for_react.backend_for_react.common.enums.Status;
import backend_for_react.backend_for_react.controller.request.UserRank.UserRankCreationRequest;
import backend_for_react.backend_for_react.controller.request.UserRank.UserRankUpdateRequest;
import backend_for_react.backend_for_react.controller.response.PageResponse;
import backend_for_react.backend_for_react.controller.response.UserRankResponse;
import backend_for_react.backend_for_react.controller.response.VoucherResponse;
import backend_for_react.backend_for_react.exception.BusinessException;
import backend_for_react.backend_for_react.exception.ErrorCode;
import backend_for_react.backend_for_react.mapper.UserRankMapper;
import backend_for_react.backend_for_react.mapper.VoucherMapper;
import backend_for_react.backend_for_react.model.UserRank;
import backend_for_react.backend_for_react.model.Voucher;
import backend_for_react.backend_for_react.repository.UserRankRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j(topic = "VOUCHER-SERVICE")
@RequiredArgsConstructor
@Service
@FieldDefaults(makeFinal = true , level = AccessLevel.PRIVATE)
public class UserRankService {
    UserRankRepository userRankRepository;

    public PageResponse<UserRankResponse> findAll(String keyword, String sort, int page, int size){
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
        Page<UserRank> userRanks = null;
        if (keyword == null || keyword.isEmpty()) {
            userRanks = userRankRepository.findAll(pageable);
        } else {
            keyword = "%" + keyword.toLowerCase() + "%";
            userRanks = userRankRepository.searchByKeyword(keyword, pageable);
        }
        PageResponse response = getUserRankPageResponse(pageNo, size, userRanks);
        return response;
    }
    public void add(UserRankCreationRequest request){
        int maxLevel = userRankRepository.findMaxLevel();
        UserRank userRank = new UserRank();
        userRank.setName(request.getName());
        userRank.setMinSpent(request.getMinSpent());
        userRank.setLevel(maxLevel + 1);
        userRank.setStatus(Status.ACTIVE);
        userRankRepository.save(userRank);
    }

    public void update(UserRankUpdateRequest request){
        UserRank userRank = userRankRepository.findById(request.getId())
                .orElseThrow(()-> new BusinessException(ErrorCode.BAD_REQUEST, "User rank not found"));
        userRank.setName(request.getName());
        userRank.setMinSpent(request.getMinSpent());
        userRankRepository.save(userRank);
    }

    public void delete(Long id){
        UserRank userRank = userRankRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST, "User rank not found"));
        userRank.setStatus(Status.INACTIVE);
        userRankRepository.save(userRank);
    }

    private PageResponse<UserRankResponse> getUserRankPageResponse(int page, int size, Page<UserRank> userRanks) {
        List<UserRankResponse> userRankResponseList = userRanks.stream()
                .map(userRank -> UserRankMapper.toUserRankResponse(userRank))
                .toList();

        PageResponse<UserRankResponse> response = new PageResponse<>();
        response.setPageNumber(page);
        response.setPageSize(size);
        response.setTotalElements(userRanks.getTotalElements());
        response.setTotalPages(userRanks.getTotalPages());
        response.setData(userRankResponseList);
        return response;
    }

}
