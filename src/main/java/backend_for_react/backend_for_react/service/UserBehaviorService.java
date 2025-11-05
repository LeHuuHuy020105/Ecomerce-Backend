package backend_for_react.backend_for_react.service;

import backend_for_react.backend_for_react.controller.request.BehaviorUser.UserBehaviorRequest;
import backend_for_react.backend_for_react.controller.response.PageResponse;
import backend_for_react.backend_for_react.exception.BusinessException;
import backend_for_react.backend_for_react.exception.ErrorCode;
import backend_for_react.backend_for_react.exception.MessageError;
import backend_for_react.backend_for_react.model.Product;
import backend_for_react.backend_for_react.model.User;
import backend_for_react.backend_for_react.model.UserBehavior;
import backend_for_react.backend_for_react.repository.ProductRepository;
import backend_for_react.backend_for_react.repository.UserBehaviorRepository;
import backend_for_react.backend_for_react.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j(topic = "BEHAVIOR-USER")
@RequiredArgsConstructor
public class UserBehaviorService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final UserBehaviorRepository userBehaviorRepository;

    /**
     * Ghi nhận hành vi người dùng
     */
    @Transactional
    public void recordBehavior(UserBehaviorRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST, MessageError.PRODUCT_NOT_FOUND));

        UserBehavior behavior = new UserBehavior();
        behavior.setProduct(product);
        behavior.setBehaviorType(request.getBehaviorType());

        if (request.getUserId() != null) {
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST, MessageError.USER_NOT_FOUND));
            behavior.setUser(user);
        }

        userBehaviorRepository.save(behavior);
    }
}
