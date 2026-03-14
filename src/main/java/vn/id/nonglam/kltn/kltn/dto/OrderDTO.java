package vn.id.nonglam.kltn.kltn.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class OrderDTO {
    public record OrderRequest(List<UUID> roomDetailsId, String note, LocalDateTime checkin, LocalDateTime checkout) {}

    public record RoomDetailSnapShotResponse(UUID id, String code, boolean valid) {}

    public record RoomDetailValidResponse(UUID roomTypeId, String name,
                                          List<RoomDetailSnapShotResponse> data) {}

    public record OrderResponse(boolean status, UUID orderId, double deposit) {}
}
