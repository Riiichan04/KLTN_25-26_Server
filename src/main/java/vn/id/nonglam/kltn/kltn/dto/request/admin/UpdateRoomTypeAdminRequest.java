package vn.id.nonglam.kltn.kltn.dto.request.admin;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record UpdateRoomTypeAdminRequest(
        UUID id,
        UUID hotelId,
        String name,
        String description,
        BigDecimal price,
        Integer capacity,
        List<String> images,
        List<UUID> roomUtilities,
        List<RoomDetailAdminRequest> roomDetails
) {
    public record RoomDetailAdminRequest(UUID id, String roomCode, Boolean isActive) {}
}