package vn.id.nonglam.kltn.kltn.dto.request.admin;

import vn.id.nonglam.kltn.kltn.common.enums.Gender;
import vn.id.nonglam.kltn.kltn.common.enums.UserRole;

public record AddUserRequest(String username, String password, String email, String displayName,
                             String avatarUrl, String phone, Gender gender, UserRole role, boolean isActive, boolean isVerified) {
}
