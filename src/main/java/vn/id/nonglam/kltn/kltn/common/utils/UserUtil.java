package vn.id.nonglam.kltn.kltn.common.utils;

import vn.id.nonglam.kltn.kltn.dto.UserDTO;
import vn.id.nonglam.kltn.kltn.models.user.User;
import vn.id.nonglam.kltn.kltn.repositories.UserRepository;
import vn.id.nonglam.kltn.kltn.security.SecurityUtil;

public interface UserUtil {
    static UserDTO convertUserToUserDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getDisplayName(),
                user.getAvatarUrl(),
                user.getGender(),
                user.getRole(),
                user.getUpdatedAt()
        );
    }
}
