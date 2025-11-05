package backend_for_react.backend_for_react.service;

import backend_for_react.backend_for_react.common.enums.PaymentStatus;
import backend_for_react.backend_for_react.common.enums.PaymentType;
import backend_for_react.backend_for_react.common.utils.SecurityUtils;
import backend_for_react.backend_for_react.config.vnpay.VnpayConfig;
import backend_for_react.backend_for_react.exception.BusinessException;
import backend_for_react.backend_for_react.exception.ErrorCode;
import backend_for_react.backend_for_react.exception.MessageError;
import backend_for_react.backend_for_react.model.Order;
import backend_for_react.backend_for_react.model.User;
import backend_for_react.backend_for_react.repository.OrderRepository;
import backend_for_react.backend_for_react.service.impl.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.service.SecurityService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "PAYMENT-SERVICE")
public class PaymentService {

    private final VnpayConfig vnpayConfig;
    private final OrderService orderService;
    private final OrderRepository orderRepository;
    private final RedisTemplate redisTemplate;
    private final SecurityUtils securityUtils;

    public String add(HttpServletRequest request , Long orderId) throws UnsupportedEncodingException {
        log.info("secretKey : {}", vnpayConfig.getSecretKey());
        log.info("vnp_TmnCode : {}" , vnpayConfig.getVnpTmnCode());
        log.info("vnp_Version: {}" , vnpayConfig.getVnpVersion());

        User user = securityUtils.getCurrentUser();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(()-> new BusinessException(ErrorCode.BAD_REQUEST, MessageError.ORDER_NOT_FOUND));

        if(order.getUser() != user){
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Order is not yours");
        }
        if(!order.getPaymentType().equals(PaymentType.BANK_TRANSFER)){
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Payment type is not BANK_TRANSFER");
        }
        if(order.getPaymentStatus().equals(PaymentStatus.PAID)){
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Order is PAID");
        }

        // VNPay yêu cầu nhân 100
        long amount = order.getTotalAmount().longValue()*100;
        String vnp_TmnCode = vnpayConfig.getVnpTmnCode();

        String vnp_TxnRef = "ORD" + orderId + "_" + System.currentTimeMillis();


        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnpayConfig.getVnpVersion());
        vnp_Params.put("vnp_Command", vnpayConfig.getVnpCommand());
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + vnp_TxnRef);
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_IpAddr", VnpayConfig.getIpAddress(request)); // BẮT BUỘC
        vnp_Params.put("vnp_OrderType", vnpayConfig.getOrderType());
        vnp_Params.put("vnp_ReturnUrl", vnpayConfig.getVnpReturnUrl());

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String createDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", createDate);

        cld.add(Calendar.MINUTE, 5);
        vnp_Params.put("vnp_ExpireDate", formatter.format(cld.getTime()));

        // ====== Build Data ======
        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString()));
                //Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.UTF_8.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }

// ====== Sinh mã HMAC SHA512 ======
        String queryUrl = query.toString();
        String vnp_SecureHash = vnpayConfig.hmacSHA512(vnpayConfig.getSecretKey(), hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;

// ====== Kết quả URL ======
        String paymentUrl = vnpayConfig.getVnpPayUrl() + "?" + queryUrl;
        log.info("✅ VNPay URL generated: {}", paymentUrl);

        redisTemplate.opsForValue().set(vnp_TxnRef,orderId,5, TimeUnit.MINUTES);
        return paymentUrl;
    }

    public boolean validateVnpayCallback(HttpServletRequest request){
        Map<String, String> fields = new HashMap<>();
        for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements();) {
            String fieldName = params.nextElement();
            String fieldValue = request.getParameter(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                fields.put(fieldName, fieldValue);
            }
        }

        String vnp_SecureHash = fields.remove("vnp_SecureHash");
        fields.remove("vnp_SecureHashType");

        String signValue = vnpayConfig.hashAllFields(fields);
        return signValue.equals(vnp_SecureHash);
    }

    public boolean vnpayCallback(HttpServletRequest request){
        Map<String, String[]> params = request.getParameterMap();
        String vnp_ResponseCode = request.getParameter("vnp_ResponseCode");
        String vnp_TxnRef = request.getParameter("vnp_TxnRef");
        String vnp_SecureHash = request.getParameter("vnp_SecureHash");

        log.info("VNPay return: vnp_ResponseCode={}, vnp_TxnRef={}, vnp_SecureHash={}",
                vnp_ResponseCode, vnp_TxnRef, vnp_SecureHash);


        boolean validSignature = validateVnpayCallback(request);

        if(!validSignature){
            throw new BusinessException(ErrorCode.BAD_REQUEST,"Wrong signature VNPay callback");
        }

        // Kiểm tra Redis xem giao dịch còn hợp lệ không
        Object cachedOrderId = redisTemplate.opsForValue().get(vnp_TxnRef);
        if (cachedOrderId == null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Payment link is expired or invalid");
        }

        if("00".equals(vnp_ResponseCode)){
            String orderIdStr = vnp_TxnRef.split("_")[0].replace("ORD", "");
            Long orderId = Long.parseLong(orderIdStr);
            orderService.completePayment(orderId);
            redisTemplate.delete(vnp_TxnRef);
            log.info("Thanh toán thành công cho đơn hàng {}", orderId);
            return true;
        }else {
            return false;
        }
    }

}
