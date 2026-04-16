package vn.id.nonglam.kltn.kltn.services.admin;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.id.nonglam.kltn.kltn.dto.request.admin.AddUserRequest;
import vn.id.nonglam.kltn.kltn.dto.request.admin.ChangeUserActiveRequest;
import vn.id.nonglam.kltn.kltn.dto.request.admin.ChangeUserRoleRequest;
import vn.id.nonglam.kltn.kltn.dto.response.admin.GetUserResponse;
import vn.id.nonglam.kltn.kltn.models.user.User;
import vn.id.nonglam.kltn.kltn.repositories.UserRepository;


@Service
@RequiredArgsConstructor
public class AdminUserService {
    private final UserRepository userRepository;

    public Page<GetUserResponse> getUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(u -> mapperUserToGetUserResponse(u));
    }

    public GetUserResponse addUser(AddUserRequest request) {
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

    private GetUserResponse mapperUserToGetUserResponse(User user) {
        return new GetUserResponse(user.getId(), user.getUsername(), user.getEmail(),
                user.getDisplayName(), user.getAvatarUrl(), user.getPhone(), user.getGender(),
                user.getRole(), user.isActive(), user.isVerified(), user.getCreatedAt(), user.getUpdatedAt());
    }

    @Transactional
    public boolean changeRole(ChangeUserRoleRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người dùng với ID: " + request.userId()));
        user.setRole(request.role());
        userRepository.save(user);
        return true;
    }

    @Transactional
    public boolean changeActive(ChangeUserActiveRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người dùng với ID: " + request.userId()));
        user.setActive(request.active());
        userRepository.save(user);
        return true;
    }
}
