package vn.id.nonglam.kltn.kltn.dto.response.ai;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record CommentReviewAspectResponse(UUID id, String content, String aspect,
                                          String sentiment, String opinionWord, LocalDateTime createdAt) {
}
