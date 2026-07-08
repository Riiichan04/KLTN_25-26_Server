package vn.id.nonglam.kltn.kltn.dto.response.order;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
        boolean status,
        UUID orderId,
        BigDecimal price
) {
    public record RoomDetailSnapShotResponse(
            UUID id,
            String code,
            boolean valid
    ) {
    }

    public record RoomDetailValidResponse(
            UUID roomTypeId,
            String name,
            BigDecimal price,
            List<RoomDetailSnapShotResponse> data
    ) {
    }
}
