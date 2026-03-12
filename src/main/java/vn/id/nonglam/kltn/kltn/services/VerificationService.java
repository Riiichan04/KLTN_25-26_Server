package vn.id.nonglam.kltn.kltn.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.id.nonglam.kltn.kltn.common.enums.VerificationType;
import vn.id.nonglam.kltn.kltn.dto.response.auth.GenerateVerifyResponse;
import vn.id.nonglam.kltn.kltn.models.auth.Verification;
import vn.id.nonglam.kltn.kltn.models.user.User;
import vn.id.nonglam.kltn.kltn.repositories.UserRepository;
import vn.id.nonglam.kltn.kltn.repositories.VerificationRepository;
import vn.id.nonglam.kltn.kltn.security.PasswordEncryption;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@Slf4j
public class VerificationService {
    private static final int LIMIT_ATTEMPT = 5;
    private static final int DEFAULT_OTP_LENGTH = 6;

    private int limitAttempt = LIMIT_ATTEMPT;
    private int otpLength = DEFAULT_OTP_LENGTH;

    private final VerificationRepository verificationRepository;
    private final UserRepository userRepository;
    private final MailService mailService;

    @Autowired
    public VerificationService(VerificationRepository verificationRepository, UserRepository userRepository, MailService mailService) {
        this.verificationRepository = verificationRepository;
        this.userRepository = userRepository;
        this.mailService = mailService;
    }


    public VerificationService(int limitAttempt, int otpLength, VerificationRepository verificationRepository, UserRepository userRepository, MailService mailService) {
        this.limitAttempt = limitAttempt;
        this.otpLength = otpLength;
        this.verificationRepository = verificationRepository;
        this.userRepository = userRepository;
        this.mailService = mailService;
    }

    @Transactional
    public GenerateVerifyResponse sendVerificationCode(String email, VerificationType type) {
        try {
            // Does user exists
            User user = userRepository.findByEmail(email);
            if (user == null) {
                return new GenerateVerifyResponse(false, "User not found");
            }
            // Does user verified if type = VERIFY_USER
            if (type == VerificationType.VERIFY_USER && user.isVerified()) {
                return new GenerateVerifyResponse(false, "User is already verified");
            }
            // Does user have active otp at the same time
            Verification existVerification = verificationRepository.getByEmail(email);
            if (existVerification != null) {
                if (existVerification.getExpiredAt().isAfter(LocalDateTime.now()) && existVerification.getType() == type ) {
                    return new GenerateVerifyResponse(false, "You already have verification code");
                }
            }

            //Create new verification code
            String otp = generateOTP(this.otpLength);
            Verification savedVerification = new Verification();
            savedVerification.setEmail(email);
            savedVerification.setType(type);
            savedVerification.setCode(otp);
            verificationRepository.save(savedVerification);

            //Send mail
            mailService.sendOtpEmail(email, otp);
            return new GenerateVerifyResponse(true, "Sent verification code successfully. Check your email to get your verification code.");
        }
        catch (Exception e) {
            return new GenerateVerifyResponse(false, "Failed when generating verification code");
        }
    }

    @Transactional
    public GenerateVerifyResponse verifyUser(String email, String otp) {
        try {
            User targetUser = userRepository.findByEmail(email);
            if (email == null) {
                return new GenerateVerifyResponse(false, "User not found");
            }

            GenerateVerifyResponse verifyResult = this.verifyCode(email, otp, VerificationType.VERIFY_USER);
            if (verifyResult.result()) {
                targetUser.setVerified(true);
            }
            return verifyResult;
        }
        catch (Exception e) {
            return new GenerateVerifyResponse(false, "Failed when verifying user");
        }
    }

    @Transactional
    public GenerateVerifyResponse verifyResetPassword(String email, String otp, String newPassword) {
        try {
            User targetUser = userRepository.findByEmail(email);
            if (email == null) {
                return new GenerateVerifyResponse(false, "User not found");
            }

            GenerateVerifyResponse verifyResult = this.verifyCode(email, otp, VerificationType.RESET_PASSWORD);
            if (verifyResult.result()) {
                targetUser.setPassword(PasswordEncryption.hashPassword(newPassword));
            }
            return verifyResult;
        }
        catch (Exception e) {
            return new GenerateVerifyResponse(false, "Failed when reset your password");
        }
    }

    private GenerateVerifyResponse verifyCode(String email, String otpCode, VerificationType type) {
        try {
            //Get current verification by email and type
            Verification targetVerification = verificationRepository.getByEmailAndType(email, type);
            if (targetVerification == null) {
                return new GenerateVerifyResponse(false, "Verification code not found");
            }
            //Check attempt
            if (targetVerification.getAttempts() >= this.limitAttempt) {
                verificationRepository.delete(targetVerification);
                return new GenerateVerifyResponse(
                        false,
                        "Maximum number of authentication attempts exceeded. Please request new verification code"
                );
            }
            //Check expired time
            if (targetVerification.getExpiredAt().isAfter(LocalDateTime.now())) {
                verificationRepository.delete(targetVerification);
                return new GenerateVerifyResponse(false, "Verification code expired");
            }

            if (!targetVerification.getCode().equals(otpCode)) {
                return new GenerateVerifyResponse(false, "Verification code does not match");
            }
            //Success
            verificationRepository.delete(targetVerification);
            return new GenerateVerifyResponse(true, "Verified successfully");
        }
        catch (Exception e) {
            return new GenerateVerifyResponse(false, "Failed when verifying code");
        }
    }

    private static String generateOTP(int length) {
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < length; i++) {
            otp.append(secureRandom.nextInt(10));
        }
        return otp.toString();
    }
}
