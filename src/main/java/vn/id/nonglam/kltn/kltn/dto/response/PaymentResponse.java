package vn.id.nonglam.kltn.kltn.dto.response;

import lombok.Builder;
import vn.id.nonglam.kltn.kltn.common.enums.OrderStatus;

@Builder
public record PaymentResponse(
        String paymentUrl,
        String orderCode,
        OrderStatus status,
        String message
) {
}
