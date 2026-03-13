package vn.id.nonglam.kltn.kltn.dto.response.comments;

import java.time.LocalDateTime;
import java.util.UUID;

public record CommentResponse(
        UUID commentId,
        String content,
        double rating,
        //User dto here
        LocalDateTime updatedAt
) {
}
