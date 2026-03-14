package vn.id.nonglam.kltn.kltn.dto;

import vn.id.nonglam.kltn.kltn.common.enums.Gender;
import vn.id.nonglam.kltn.kltn.common.enums.UserRole;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserDTO(
        UUID userId,
        String username,
        String email,
        String displayName,
        String avatarUrl,
        Gender gender,
        UserRole role,
        LocalDateTime updatedAt
) {
}
