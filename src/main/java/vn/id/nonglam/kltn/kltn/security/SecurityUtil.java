package vn.id.nonglam.kltn.kltn.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import vn.id.nonglam.kltn.kltn.models.auth.UserPrinciple;

import java.util.Optional;
import java.util.UUID;

@Component("securityUtil")
public class SecurityUtil {

    public static Optional<UserPrinciple> getCurrentUser() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getPrincipal)
                .filter(p -> p instanceof UserPrinciple)
                .map(p -> (UserPrinciple) p);
    }

    public static Optional<UUID> currentUserId() {
        return getCurrentUser().map(UserPrinciple::id);
    }
}