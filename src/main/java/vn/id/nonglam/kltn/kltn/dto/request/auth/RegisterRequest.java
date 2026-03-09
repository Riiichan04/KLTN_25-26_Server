package vn.id.nonglam.kltn.kltn.dto.request.auth;

public record RegisterRequest(
        String email,
        String username,
        String password
) {
}
