package vn.id.nonglam.kltn.kltn.dto.response.auth;

public record AuthResponse(
        boolean result,
        String message,
        AuthDto authDto
) { }
