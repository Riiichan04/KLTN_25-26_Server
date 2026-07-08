package vn.id.nonglam.kltn.kltn.models.auth;

import vn.id.nonglam.kltn.kltn.common.enums.UserRole;

import java.security.Principal;
import java.util.UUID;

public record UserPrinciple(
        UUID id,
        UserRole role
) implements Principal {

    @Override
    public String getName() {
        return id.toString();
    }
}