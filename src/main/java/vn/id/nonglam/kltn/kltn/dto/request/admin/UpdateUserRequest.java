package vn.id.nonglam.kltn.kltn.dto.request.admin;

import vn.id.nonglam.kltn.kltn.common.enums.UserRole;

import java.util.UUID;

public record UpdateUserRequest(UUID id, Boolean active, UserRole role) {}
