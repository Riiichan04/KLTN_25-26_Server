package vn.id.nonglam.kltn.kltn.dto.response.order;

import vn.id.nonglam.kltn.kltn.common.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record HistoryOrderResponse(
        UUID id,
        HotelResponse hotel,
        String note,
        LocalDateTime checkin,
        LocalDateTime checkout,
        OrderStatus status,
        LocalDateTime createdAt,
        List<RoomTypeSnapShotResponse> data
) {
    public record RoomDetailSnapShotResponse (
            UUID id,
            String code,
            BigDecimal actualPrice
    ) {
    }

    public record RoomTypeSnapShotResponse (
            UUID id,
            String name,
            List<RoomDetailSnapShotResponse> data
    ) {
    }

    public record HotelResponse(UUID id, String name, String thumbnail) {}
}
