package vn.id.nonglam.kltn.kltn.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import vn.id.nonglam.kltn.kltn.common.enums.UserRole;
import vn.id.nonglam.kltn.kltn.repositories.CommentRepository;
import vn.id.nonglam.kltn.kltn.repositories.UserRepository;

import java.util.Objects;
import java.util.UUID;

@Component("securityUtil")
public class SecurityUtil {
    private CommentRepository commentRepository;
    private UserRepository userRepository;

    public boolean isUpdateComment(UUID commentId) {
        String currentUsername = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        //Admin can elete
        return commentRepository.findById(commentId)
                .map(c -> c.getUser().getUsername().equals(currentUsername))
                .orElse(false);
    }

    public boolean isDeleteComment(UUID commentId) {
        String currentUsername = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        //Admin can elete
        if (userRepository.findByUsername(currentUsername).getRole() == UserRole.ADMIN) return true;
        return commentRepository.findById(commentId)
                .map(c -> c.getUser().getUsername().equals(currentUsername))
                .orElse(false);
    }
}