package vn.id.nonglam.kltn.kltn.services.payment;

import vn.id.nonglam.kltn.kltn.common.enums.PaymentMethod;
import vn.id.nonglam.kltn.kltn.dto.response.PaymentResponse;
import vn.id.nonglam.kltn.kltn.models.order.Order;
import vn.id.nonglam.kltn.kltn.repositories.OrderRepository;
import vn.id.nonglam.kltn.kltn.repositories.PaymentRepository;

import java.math.BigDecimal;
import java.util.Map;

public abstract class PaymentService {
    protected OrderRepository orderRepository;
    protected PaymentRepository paymentRepository;

    public abstract PaymentMethod getPaymentMethod();
    public abstract PaymentResponse executePurchase(Order order);
    public abstract void handleWebhook(Map<String, Object> payload);
    public abstract String generateQRCode(BigDecimal amount);
}
