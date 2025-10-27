package backend_for_react.backend_for_react.service;

import backend_for_react.backend_for_react.common.utils.SecurityUtils;
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
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE , makeFinal = true)
public class FavoriteService {
    UserRepository userRepository;
    ProductRepository productRepository;
    SecurityUtils securityUtils;


    public void addFavorite(Long productId) {
        User user = securityUtils.getCurrentUser();
        Product product = productRepository.findById(productId)
                .orElseThrow(()-> new BusinessException(ErrorCode.BAD_REQUEST , MessageError.PRODUCT_NOT_FOUND));
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

    public List<ProductBaseResponse> getFavorites() {
        User user = securityUtils.getCurrentUser();
        return user.getFavoriteProducts().stream().map(product -> ProductMapper.toBaseResponse(product)).toList();
    }
}
