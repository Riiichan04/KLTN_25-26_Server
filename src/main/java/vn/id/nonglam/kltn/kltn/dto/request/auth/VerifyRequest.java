package vn.id.nonglam.kltn.kltn.dto.request.auth;

public record VerifyRequest(
        String email,
        String code,
        String newPassword
) {
}
