package backend_for_react.backend_for_react.common.enums;

public enum PaymentType {
    CASH,            // Thanh toán tiền mặt
    CREDIT_CARD,     // Thanh toán qua thẻ tín dụng
    DEBIT_CARD,      // Thanh toán qua thẻ ghi nợ
    BANK_TRANSFER,   // Chuyển khoản ngân hàng
    E_WALLET,        // Ví điện tử (Momo, ZaloPay, v.v.)
    COD              // Thanh toán khi nhận hàng (Cash on Delivery)
}
