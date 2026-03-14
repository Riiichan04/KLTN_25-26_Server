package vn.id.nonglam.kltn.kltn.dto.response.comments;

import vn.id.nonglam.kltn.kltn.dto.UserDTO;

import java.time.LocalDateTime;
import java.util.UUID;

public record CommentResponse(
        UUID commentId,
        String content,
        double rating,
        UserDTO user,
        LocalDateTime updatedAt
) {
}
