package vn.id.nonglam.kltn.kltn.dto.request.ai;

import lombok.Builder;

import java.util.List;

@Builder
public record BatchPredictRequest(
        List<ReviewItem> reviews
) {}