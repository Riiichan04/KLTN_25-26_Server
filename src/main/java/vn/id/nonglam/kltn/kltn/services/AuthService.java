package vn.id.nonglam.kltn.kltn.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.id.nonglam.kltn.kltn.common.enums.UserRole;
import vn.id.nonglam.kltn.kltn.dto.request.auth.LoginRequest;
import vn.id.nonglam.kltn.kltn.dto.request.auth.RegisterOwnerRequest;
import vn.id.nonglam.kltn.kltn.dto.request.auth.RegisterRequest;
import vn.id.nonglam.kltn.kltn.dto.response.auth.AuthDto;
import vn.id.nonglam.kltn.kltn.dto.response.auth.AuthResponse;
import vn.id.nonglam.kltn.kltn.models.user.User;
import vn.id.nonglam.kltn.kltn.models.user.UserPaymentDetail;
import vn.id.nonglam.kltn.kltn.repositories.UserPaymentDetailRepository;
import vn.id.nonglam.kltn.kltn.repositories.UserRepository;
import vn.id.nonglam.kltn.kltn.security.JwtTokenProvider;
import vn.id.nonglam.kltn.kltn.security.PasswordEncryption;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtConfig;
    private final UserPaymentDetailRepository userPaymentDetailRepository;

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
        newUser.setRole(UserRole.USER);
        this.userRepository.save(newUser);
        return new AuthResponse(true, "Register success!", null);
    }

    @Transactional
    public AuthResponse registerOwner(RegisterOwnerRequest request) {
        User existUser = this.userRepository.findByEmail(request.email());
        if (existUser != null) {
            return new AuthResponse(false, "Account already exists", null);
        }

        User user = new User();
        user.setUsername(request.username());
        user.setPassword(PasswordEncryption.hashPassword(request.password()));
        user.setEmail(request.email());
        user.setDisplayName(request.displayName());
        user.setPhone(request.phone());
        user.setGender(request.gender());
        user.setUserType(request.userType());

        user.setRole(UserRole.OWNER);
        user.setVerified(false);

        UserPaymentDetail paymentDetail = new UserPaymentDetail();
        paymentDetail.setUser(user);
        paymentDetail.setBankAccount(request.bankAccount());
        paymentDetail.setBankCode(request.bankCode());
        paymentDetail.setOwnerName(request.bankOwnerName());

        User savedUser = userRepository.save(user);
        String jwtToken = jwtConfig.generateToken(savedUser.getId(), savedUser.getRole());
        AuthDto dto = getAuthDto(savedUser, jwtToken);
        return new AuthResponse(true, "Register success!", dto);
    }

    private static AuthDto getAuthDto(User targetUser, String jwtToken) {
        AuthDto dto = new AuthDto();
        dto.setId(targetUser.getId());
        dto.setActive(targetUser.isActive());
        dto.setAvatar(targetUser.getAvatarUrl());
        dto.setDisplayName(targetUser.getDisplayName());
        dto.setEmail(targetUser.getEmail());
        dto.setActive(targetUser.isActive());
        dto.setVerified(targetUser.isVerified());
        dto.setRole(targetUser.getRole());
        dto.setUsername(targetUser.getUsername());
        dto.setGender(targetUser.getGender());
        dto.setPhone(targetUser.getPhone());
        dto.setVerified(targetUser.isVerified());
        dto.setJwtToken(jwtToken);
        return dto;
    }
}
