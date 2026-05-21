package vn.id.nonglam.kltn.kltn.dto.request.admin;

import java.util.UUID;

public record ChangeUserActiveRequest(UUID userId, boolean active) {
}
