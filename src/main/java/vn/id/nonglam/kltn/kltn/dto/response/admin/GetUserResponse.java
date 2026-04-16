package vn.id.nonglam.kltn.kltn.dto.response.admin;

import vn.id.nonglam.kltn.kltn.common.enums.Gender;
import vn.id.nonglam.kltn.kltn.common.enums.UserRole;

import java.time.LocalDateTime;
import java.util.UUID;

public record GetUserResponse(UUID id, String username, String email, String displayName,
                              String avatarUrl, String phone, Gender gender, UserRole role, boolean isActive, boolean isVerified,
                              LocalDateTime createdAt, LocalDateTime updatedAt) {}
