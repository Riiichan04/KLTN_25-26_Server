package vn.id.nonglam.kltn.kltn.dto.request.admin;

import vn.id.nonglam.kltn.kltn.common.enums.OrderStatus;

public record ChangeOrderStatusRequest(OrderStatus orderStatus) {
}
