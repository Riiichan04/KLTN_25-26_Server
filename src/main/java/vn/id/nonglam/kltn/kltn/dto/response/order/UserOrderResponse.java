package vn.id.nonglam.kltn.kltn.dto.response.order;

import vn.id.nonglam.kltn.kltn.common.enums.OrderStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record UserOrderResponse(
        UUID id,
        OrderStatus status,
        BigDecimal price
) {
}
