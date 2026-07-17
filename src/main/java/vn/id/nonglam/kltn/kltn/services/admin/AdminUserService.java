package vn.id.nonglam.kltn.kltn.services.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.id.nonglam.kltn.kltn.dto.request.admin.AddUserRequest;
import vn.id.nonglam.kltn.kltn.dto.request.admin.UpdateUserRequest;
import vn.id.nonglam.kltn.kltn.dto.response.admin.AdminUserResponse;
import vn.id.nonglam.kltn.kltn.models.user.User;
import vn.id.nonglam.kltn.kltn.repositories.UserRepository;

import java.util.NoSuchElementException;


@Service
@RequiredArgsConstructor
public class AdminUserService {
    private final UserRepository userRepository;

    public Page<AdminUserResponse> getUsers(String keyword, Pageable pageable) {
        return userRepository.findAll(keyword, pageable)
                .map(u -> mapperUserToGetUserResponse(u));
    }

    public AdminUserResponse addUser(AddUserRequest request) {
        User user = mapperAddUserRequestToUser(request);
        user = userRepository.save(user);
        return mapperUserToGetUserResponse(user);
    }

    private User mapperAddUserRequestToUser(AddUserRequest request) {
        User user = new User();
        user.setUsername(request.username());
        user.setPassword(request.password());
        user.setEmail(request.email());
        user.setDisplayName(request.displayName());
        user.setAvatarUrl(request.avatarUrl());
        user.setPhone(request.phone());
        user.setGender(request.gender());
        user.setRole(request.role());
        user.setActive(request.isActive());
        user.setVerified(request.isVerified());
        return user;
    }

    private AdminUserResponse mapperUserToGetUserResponse(User user) {
        return new AdminUserResponse(user.getId(), user.getUsername(), user.getEmail(),
                user.getDisplayName(), user.getAvatarUrl(), user.getPhone(), user.getGender(),
                user.getRole(), user.isActive(), user.isVerified(), user.getCreatedAt(), user.getUpdatedAt());
    }

    @Transactional
    public AdminUserResponse updateUser(UpdateUserRequest request) {
        User user = userRepository.findById(request.id())
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy người dùng với ID: " + request.id()));

        user.setRole(request.role());
        user.setActive(request.active());

        user = userRepository.save(user);
        return mapperUserToGetUserResponse(user);
    }

}
