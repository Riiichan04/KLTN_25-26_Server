package vn.id.nonglam.kltn.kltn.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import vn.id.nonglam.kltn.kltn.common.enums.UserRole;
import vn.id.nonglam.kltn.kltn.repositories.CommentRepository;
import vn.id.nonglam.kltn.kltn.repositories.UserRepository;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Component("securityUtil")
public class SecurityUtil {
    public static Optional<UUID> currentUserId() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getPrincipal)
                .filter(p -> p instanceof UUID)
                .map(p -> (UUID) p);
    }
}