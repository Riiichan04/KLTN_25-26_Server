package vn.id.nonglam.kltn.kltn.dto.response.order;

import java.util.List;
import java.util.UUID;

public record OrderResponse(boolean status, UUID orderId, double deposit) {
    public record RoomDetailSnapShotResponse(UUID id, String code, boolean valid) {}

    public record RoomDetailValidResponse(UUID roomTypeId, String name,
                                          List<RoomDetailSnapShotResponse> data) {}
}
