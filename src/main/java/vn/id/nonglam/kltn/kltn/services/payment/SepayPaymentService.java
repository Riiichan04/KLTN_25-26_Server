package vn.id.nonglam.kltn.kltn.services.payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vn.id.nonglam.kltn.kltn.common.enums.OrderStatus;
import vn.id.nonglam.kltn.kltn.common.enums.PaymentMethod;
import vn.id.nonglam.kltn.kltn.common.enums.PaymentStatus;
import vn.id.nonglam.kltn.kltn.dto.response.PaymentResponse;
import vn.id.nonglam.kltn.kltn.models.order.Order;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SepayPaymentService extends PaymentService {
    @Override
    public PaymentMethod getPaymentMethod() {
        return null;
    }

    @Override
    public PaymentResponse executePurchase(Order order) {
        return null;
    }

    @Override
    public void handleWebhook(Map<String, Object> payload) {

    }

    @Override
    public String generateQRCode(BigDecimal amount) {
        return "";
    }
//    @Value("${payment.qr}")
//    private String QR_CODE_URL;
//
//    @Override
//    public PaymentMethod getPaymentMethod() {
//        return PaymentMethod.SEPAY_BANK_TRANSFER;
//    }
//
//    @Override
//    public PaymentResponse executePurchase(Order order) {
//        BigDecimal amount = order.calculateTotalAmount();
//        String orderCode = String.valueOf(order.getId());
//
//        String qrUrl = generateQRCode(amount);
//        return PaymentResponse.builder()
//                .paymentUrl(qrUrl)
//                .orderCode(orderCode)
//                .status(OrderStatus.PENDING)
//                .message("Vui lòng quét mã QR để thanh toán")
//                .build();
//    }
//
//    public void handleWebhook(Map<String, Object> payload) {
//        Object transactionIdObj = payload.get("id");
//        if (transactionIdObj == null) {
//            log.error("Webhook không hợp lệ: Thiếu transaction ID");
//            return;
//        }
//        String transactionId = String.valueOf(transactionIdObj);
//        //Extract order code
//        Object codeObj = payload.get("code");
//        if (codeObj == null) {  //order code can be null but still able return code 200
//            log.warn("Giao dịch SePay ID [{}] không nhận diện được mã đơn hàng. Cần đối soát thủ công.", transactionId);
//            return;
//        }
//        String orderCodeStr = String.valueOf(codeObj);
//
//        //Get transfer amount
//        Object amountObj = payload.get("transferAmount");
//        if (amountObj == null) return;
//        BigDecimal transferAmount = new BigDecimal(String.valueOf(amountObj));
//
//        UUID orderId;
//        try {
//            orderId = UUID.fromString(orderCodeStr);
//        } catch (Exception e) {
//            log.warn("Mã đơn hàng không đúng định dạng: {}", orderCodeStr);
//            return;
//        }
//
//        Order order = orderRepository.findById(orderId).orElse(null);
//        if (order == null) {
//            log.warn("Không tìm thấy đơn hàng ID: {} trong hệ thống.", orderId);
//            return;
//        }
//
//        if (order.getPaymentStatus() == PaymentStatus.PAID) {
//            log.info("Đơn hàng {} đã được thanh toán trước đó. Bỏ qua Webhook ID [{}]", orderId, transactionId);
//            return;
//        }
//
//        BigDecimal expectedAmount = order.calculateTotalAmount();
//        if (transferAmount.compareTo(expectedAmount) >= 0) {
//            order.setPaymentStatus(PaymentStatus.PAID);
//            // order.setProviderTransactionId(transactionId);
//
//            log.info("Xác nhận thanh toán ĐỦ tiền cho đơn {}. Nhận: {} VND", orderId, transferAmount);
//            // TODO: put WalletService to put money after calculated to owner
//        } else {
//            order.setPaymentStatus(PaymentStatus.PARTIAL_PAID); // Incomplete payment, can be continue
//            log.warn("Đơn {} thanh toán THIẾU. Yêu cầu: {}, Nhận: {}", orderId, expectedAmount, transferAmount);
//        }
//
//        orderRepository.save(order);
//    }
//
//    @Override
//    public String generateQRCode(BigDecimal amount) {
//        return QR_CODE_URL + "?amount=" + amount.toPlainString();
//    }

}
