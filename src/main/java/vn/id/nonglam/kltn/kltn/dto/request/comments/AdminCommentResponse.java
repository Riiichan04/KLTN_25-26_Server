package vn.id.nonglam.kltn.kltn.dto.request.comments;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record AdminCommentResponse(UUID id, String username,
                                   String email, String avatarUrl, String content,
                                   double rating, boolean isActive,
                                   List<SentimentAspectResponse> sentimentAspects,
                                   LocalDateTime createdAt, LocalDateTime updatedAt) {
    @Builder
    public record SentimentAspectResponse(
            UUID id, String aspect, String sentiment, String opinionWord, LocalDateTime createdAt, LocalDateTime updatedAt
    ) {}
}
