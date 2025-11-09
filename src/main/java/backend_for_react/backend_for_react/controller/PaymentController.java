package backend_for_react.backend_for_react.controller;

import backend_for_react.backend_for_react.config.vnpay.VnpayConfig;
import backend_for_react.backend_for_react.controller.response.ApiResponse;
import backend_for_react.backend_for_react.service.PaymentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
@Slf4j(topic = "PAYMENT-CONTROLLER")
@Tag(name = "PAYMENT CONTROLLER")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentController {
    PaymentService paymentService;

    @PostMapping("/{orderId}/add")
    public ApiResponse<Object> addPayment(HttpServletRequest request , @PathVariable Long orderId) throws UnsupportedEncodingException {
        String result = paymentService.add(request,orderId);
        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Add payment successfully ")
                .data(result)
                .build();
    }

    @GetMapping("/vnpay-return")
        public ApiResponse<Boolean> returnPayment(HttpServletRequest request) throws IOException {
        boolean success = paymentService.vnpayCallback(request); // sửa hàm này trả boolean
        return ApiResponse.<Boolean>builder()
                .status(HttpStatus.OK.value())
                .message("Return payment ")
                .data(success)
                .build();
    }
}
