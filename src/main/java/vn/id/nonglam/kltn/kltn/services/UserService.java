package vn.id.nonglam.kltn.kltn.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import vn.id.nonglam.kltn.kltn.dto.request.auth.UpdateRequest;
import vn.id.nonglam.kltn.kltn.dto.response.auth.AuthDto;
import vn.id.nonglam.kltn.kltn.models.user.User;
import vn.id.nonglam.kltn.kltn.repositories.UserRepository;
import vn.id.nonglam.kltn.kltn.security.SecurityUtil;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public AuthDto update(UpdateRequest input) {
        UUID userId = SecurityUtil.currentUserId().orElseThrow(() ->
                new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not logged in"));

        User user = userRepository.findUserByIdAndIsActive(userId, true);
        if (user == null) return null;

        user.setAvatarUrl(input.avatarUrl());
        user.setDisplayName(input.displayName());
        user.setPhone(input.phone());
        user.setGender(input.gender());
        user = userRepository.save(user);
        return getAuthDto(user);
    }

    private static AuthDto getAuthDto(User targetUser) {
        return AuthDto.builder()
                .id(targetUser.getId())
                .active(targetUser.isActive())
                .avatar(targetUser.getAvatarUrl())
                .displayName(targetUser.getDisplayName())
                .email(targetUser.getEmail())
                .verified(targetUser.isVerified())
                .role(targetUser.getRole())
                .username(targetUser.getUsername())
                .gender(targetUser.getGender())
                .phone(targetUser.getPhone())
                .build();
    }
}
