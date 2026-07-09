package vn.id.nonglam.kltn.kltn.dto.request.order;

import vn.id.nonglam.kltn.kltn.common.enums.OrderStatus;

public record UpdateOrderStatusRequest(
        OrderStatus status
) {
}
