package vn.id.nonglam.kltn.kltn.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.id.nonglam.kltn.kltn.dto.request.auth.LoginRequest;
import vn.id.nonglam.kltn.kltn.dto.request.auth.RegisterRequest;
import vn.id.nonglam.kltn.kltn.dto.response.auth.AuthDto;
import vn.id.nonglam.kltn.kltn.dto.response.auth.AuthResponse;
import vn.id.nonglam.kltn.kltn.models.user.User;
import vn.id.nonglam.kltn.kltn.repositories.UserRepository;
import vn.id.nonglam.kltn.kltn.security.JwtTokenProvider;
import vn.id.nonglam.kltn.kltn.security.PasswordEncryption;

@Service
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtConfig;

    @Autowired
    public AuthService(UserRepository userRepository, JwtTokenProvider jwtConfig) {
        this.userRepository = userRepository;
        this.jwtConfig = jwtConfig;
    }

    public AuthResponse login(LoginRequest input) {
        User targetUser = userRepository.findByEmail(input.email());
        if (targetUser == null) {
            return new AuthResponse(false, "Wrong password or email", null);
        }
        boolean isPasswordMatch = PasswordEncryption.checkPassword(input.password(), targetUser.getPassword());
        if (!isPasswordMatch) {
            return new AuthResponse(false, "Wrong password or email", null);
        }
        String jwtToken = jwtConfig.generateToken(targetUser.getId(), targetUser.getRole());
        AuthDto dto = getAuthDto(targetUser, jwtToken);
        return new AuthResponse(true, "Login success!", dto);
    }

    @Transactional
    public AuthResponse register(RegisterRequest input) {
        User existUser = this.userRepository.findByEmail(input.email());
        if (existUser != null) {
            return new AuthResponse(false, "Account already exists", null);
        }
        User newUser = new User();
        newUser.setEmail(input.email());
        newUser.setUsername(input.username());
        newUser.setPassword(PasswordEncryption.hashPassword(input.password()));
        this.userRepository.save(newUser);
        return new AuthResponse(true, "Register success!", null);
    }

    private static AuthDto getAuthDto(User targetUser, String jwtToken) {
        AuthDto dto = new AuthDto();
        dto.setId(targetUser.getId());
        dto.setActive(targetUser.isActive());
        dto.setAvatar(targetUser.getAvatarUrl());
//        dto.setDescription(targetUser.getDescription());
        dto.setDisplayName(targetUser.getDisplayName());
        dto.setEmail(targetUser.getEmail());
//        dto.setGender(targetUser.getGender());
        dto.setRole(targetUser.getRole());
        dto.setUsername(targetUser.getUsername());
        dto.setVerified(targetUser.isVerified());
        dto.setJwtToken(jwtToken);
        return dto;
    }
}
