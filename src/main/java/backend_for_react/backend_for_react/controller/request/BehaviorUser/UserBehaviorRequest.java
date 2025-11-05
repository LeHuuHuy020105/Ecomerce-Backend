package backend_for_react.backend_for_react.controller.request.BehaviorUser;

import backend_for_react.backend_for_react.common.enums.BehaviorType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserBehaviorRequest {
    private Long userId;              // null nếu chưa đăng nhập
    private Long productId;           // id sản phẩm tương tác
    private BehaviorType behaviorType; // VIEW, ADD_TO_CART, PURCHASE, SEARCH
}