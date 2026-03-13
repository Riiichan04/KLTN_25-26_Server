package vn.id.nonglam.kltn.kltn.dto.response.comments;

import vn.id.nonglam.kltn.kltn.common.enums.CommentSentiment;

public record SentimentResponse(
        boolean result,
        String message,
        CommentSentiment sentiment
) {
}
