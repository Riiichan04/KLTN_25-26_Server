package vn.id.nonglam.kltn.kltn.dto.request.ai;

import lombok.Builder;

import java.util.UUID;

@Builder
public record ReviewItem(
        UUID id,
        String text
) {}