package vn.id.nonglam.kltn.kltn.dto.request.admin;

import vn.id.nonglam.kltn.kltn.common.enums.UserRole;

import java.util.UUID;

public record ChangeUserRoleRequest(UUID userId, UserRole role) {}
