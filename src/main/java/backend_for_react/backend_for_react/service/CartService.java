package backend_for_react.backend_for_react.service;

import backend_for_react.backend_for_react.common.enums.Status;
import backend_for_react.backend_for_react.common.utils.SecurityUtils;
import backend_for_react.backend_for_react.controller.request.Cart.CartCreationRequest;
import backend_for_react.backend_for_react.controller.request.Cart.CartUpdateRequest;
import backend_for_react.backend_for_react.controller.response.CartResponse;
import backend_for_react.backend_for_react.controller.response.PageResponse;
import backend_for_react.backend_for_react.controller.response.ProductVariantResponse;
import backend_for_react.backend_for_react.controller.response.UserResponse;
import backend_for_react.backend_for_react.exception.BusinessException;
import backend_for_react.backend_for_react.exception.ErrorCode;
import backend_for_react.backend_for_react.exception.MessageError;
import backend_for_react.backend_for_react.mapper.ProductMapper;
import backend_for_react.backend_for_react.mapper.ProductVariantMapper;
import backend_for_react.backend_for_react.model.Cart;
import backend_for_react.backend_for_react.model.Product;
import backend_for_react.backend_for_react.model.ProductVariant;
import backend_for_react.backend_for_react.model.User;
import backend_for_react.backend_for_react.repository.CartRepository;
import backend_for_react.backend_for_react.repository.ProductRepository;
import backend_for_react.backend_for_react.repository.ProductVariantRepository;
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
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j(topic = "CART-SERVICE")
@RequiredArgsConstructor
@Service
@FieldDefaults(makeFinal = true , level = AccessLevel.PRIVATE)
public class CartService {
    CartRepository cartRepository;
    ProductRepository productRepository;
    SecurityUtils securityUtils;
    ProductVariantRepository productVariantRepository;
    public PageResponse<CartResponse> getCarts(String sort , int page , int size){
        log.info("---Find All---");

        User user = securityUtils.getCurrentUser();

        Sort order = Sort.by(Sort.Direction.ASC, "id");
        if(sort != null && !sort.isEmpty()){
            Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
            Matcher matcher = pattern.matcher(sort);
            if(matcher.find()){
                String columnName = matcher.group(1);
                if(matcher.group(3).equalsIgnoreCase("asc")){
                    order = Sort.by(Sort.Direction.ASC,columnName);
                }else {
                    order = Sort.by(Sort.Direction.DESC,columnName);
                }
            }
        }
        int pageNo = 0;
        if (page > 0) {
            pageNo = page - 1;
        }
        Pageable pageable = PageRequest.of(pageNo, size, order);
        Page<Cart> carts = cartRepository.findAllByUser(user,pageable);
        PageResponse response = getCartPageResponse(pageNo, size, carts);
        return response;
    }
    public void add(CartCreationRequest request){
        User user = securityUtils.getCurrentUser();
        ProductVariant productVariant = productVariantRepository.findById(request.getProductVariantId())
                .orElseThrow(()-> new BusinessException(ErrorCode.BAD_REQUEST, MessageError.PRODUCT_VARIANT_NOT_FOUND));
        if(request.getQuantity() >= productVariant.getQuantity()){
            throw new BusinessException(ErrorCode.BAD_REQUEST,"Product variant exceeds quantiy");
        }
        Optional<Cart> cart = cartRepository.findByUserAndProductVariant(user, productVariant);
           if(cart.isEmpty()){
               Cart newCart = new Cart();
               newCart.setUser(user);
               newCart.setProductVariant(productVariant);
               newCart.setQuantity(request.getQuantity());
               newCart.setStatus(Status.ACTIVE);
               newCart.setListPriceSnapShot(productVariant.getPrice());
               newCart.setUrlImageSnapShot(productVariant.getProduct().getUrlCoverImage());
               newCart.setNameProductSnapShot(ProductVariantMapper.buildVariantName(productVariant));
               cartRepository.save(newCart);
           }else {
               Cart oldCart = cart.get();
               oldCart.setQuantity(oldCart.getQuantity()+ request.getQuantity());
               cartRepository.save(oldCart);
           }
    }

    public void update(Long cartId, CartUpdateRequest request){
        User user = securityUtils.getCurrentUser();
        Cart cart = cartRepository.findByIdAndStatus(cartId,Status.ACTIVE)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXISTED, "Cart not found"));
        if(cart.getUser() != user){
            throw new BusinessException(ErrorCode.NOT_EXISTED, "Cart not part of yours");
        }
        if(request.getQuantity() != null){
            if(request.getQuantity() > cart.getProductVariant().getQuantity()){
                throw new BusinessException(ErrorCode.BAD_REQUEST,"Quantity exceeds maximum quantity");
            }
            cart.setQuantity(request.getQuantity());
        }
        cartRepository.save(cart);
    }

    public void delete(Long cartId){
        User user = securityUtils.getCurrentUser();
        Cart cart = cartRepository.findByIdAndStatus(cartId,Status.ACTIVE)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXISTED, "Cart not found"));
        if(cart.getUser() != user){
            throw new BusinessException(ErrorCode.NOT_EXISTED, "Cart not part of yours");
        }
        cart.setStatus(Status.INACTIVE);
        cartRepository.save(cart);
    }
    public CartResponse getCartResponse(Cart cart){
        ProductVariantResponse productVariantResponse = ProductVariantMapper.getProductVariantResponse(cart.getProductVariant());
        return CartResponse.builder()
                .id(cart.getId())
                .productBaseResponse(ProductMapper.toBaseResponse(cart.getProductVariant().getProduct()))
                .productVariantResponse(productVariantResponse)
                .quantity(cart.getQuantity())
                .urlImageSnapShot(cart.getUrlImageSnapShot())
                .nameProductSnapShot(cart.getNameProductSnapShot())
                .listPriceSnapShot(cart.getListPriceSnapShot())
                .variantAttributesSnapshot(cart.getVariantAttributesSnapshot())
                .build();
    }
    private PageResponse<CartResponse> getCartPageResponse(int page, int size, Page<Cart> carts) {
        List<CartResponse> cartList = carts.stream()
                .map(this::getCartResponse)
                .toList();

        PageResponse<CartResponse> response = new PageResponse<>();
        response.setPageNumber(page);
        response.setPageSize(size);
        response.setTotalElements(carts.getTotalElements());
        response.setTotalPages(carts.getTotalPages());
        response.setData(cartList);
        return response;
    }
}
