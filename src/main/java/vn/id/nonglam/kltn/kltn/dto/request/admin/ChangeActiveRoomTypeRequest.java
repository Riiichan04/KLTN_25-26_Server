package vn.id.nonglam.kltn.kltn.dto.request.admin;

import java.util.UUID;

public record ChangeActiveRoomTypeRequest(UUID id, boolean active) {
}
