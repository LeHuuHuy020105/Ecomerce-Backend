package backend_for_react.backend_for_react.service;

import backend_for_react.backend_for_react.common.enums.ProductStatus;
import backend_for_react.backend_for_react.common.utils.SecurityUtils;
import backend_for_react.backend_for_react.controller.response.PageResponse;
import backend_for_react.backend_for_react.controller.response.ProductBaseResponse;
import backend_for_react.backend_for_react.exception.BusinessException;
import backend_for_react.backend_for_react.exception.ErrorCode;
import backend_for_react.backend_for_react.exception.MessageError;
import backend_for_react.backend_for_react.mapper.ProductMapper;
import backend_for_react.backend_for_react.model.Product;
import backend_for_react.backend_for_react.model.User;
import backend_for_react.backend_for_react.repository.ProductRepository;
import backend_for_react.backend_for_react.repository.UserRepository;
import backend_for_react.backend_for_react.service.impl.ProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE , makeFinal = true)
public class FavoriteService {
    UserRepository userRepository;
    ProductRepository productRepository;
    SecurityUtils securityUtils;

    public PageResponse<ProductBaseResponse> findAll(String keyword, String sort, int page, int size) {
        User user = securityUtils.getCurrentUser();
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
            products = productRepository.findFavoriteProductsByUserId(user.getId(),pageable);
        } else {
            keyword = "%" + keyword.toLowerCase() + "%";
            products = productRepository.findFavoriteProductsByUserIdAndKeyword(user.getId(),keyword, pageable);
        }
        PageResponse response = getProductPageResponse(pageNo, size, products);
        return response;
    }
    public void addFavorite(Long productId) {
        User user = securityUtils.getCurrentUser();
        Product product = productRepository.findById(productId)
                .orElseThrow(()-> new BusinessException(ErrorCode.BAD_REQUEST , MessageError.PRODUCT_NOT_FOUND));
        boolean alreadyFavorited = user.getFavoriteProducts()
                .stream()
                .anyMatch(p -> p.getId().equals(productId));
        if(alreadyFavorited){
            throw new BusinessException(ErrorCode.BAD_REQUEST,"Product is already favorited");
        }
        user.getFavoriteProducts().add(product);
        userRepository.save(user);
    }

    public void removeFavorite(Long productId) {
        User user = securityUtils.getCurrentUser();
        Product product = productRepository.findById(productId)
                .orElseThrow(()-> new BusinessException(ErrorCode.BAD_REQUEST , MessageError.PRODUCT_NOT_FOUND));
        user.getFavoriteProducts().remove(product);
        userRepository.save(user);
    }

    private PageResponse<ProductBaseResponse> getProductPageResponse(int page, int size, Page<Product> products) {
        List<ProductBaseResponse> productList = products.stream()
                .map(ProductMapper::toBaseResponse)
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
