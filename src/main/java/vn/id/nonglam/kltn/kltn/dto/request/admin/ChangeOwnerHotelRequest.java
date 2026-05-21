package vn.id.nonglam.kltn.kltn.dto.request.admin;

import java.util.UUID;

public record ChangeOwnerHotelRequest(UUID userId, UUID hotelId) {
}
