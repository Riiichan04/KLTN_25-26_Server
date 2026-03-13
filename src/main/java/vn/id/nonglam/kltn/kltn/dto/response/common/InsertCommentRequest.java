package vn.id.nonglam.kltn.kltn.dto.response.common;

import java.util.UUID;

public record InsertCommentRequest(
        UUID hotelId,
        UUID userId,
        String content,
        double rating,
        UUID parentId
) {
}
