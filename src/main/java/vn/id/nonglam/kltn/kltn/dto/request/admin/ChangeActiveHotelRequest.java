package vn.id.nonglam.kltn.kltn.dto.request.admin;

import java.util.UUID;

public record ChangeActiveHotelRequest(UUID id, boolean active) {
}
