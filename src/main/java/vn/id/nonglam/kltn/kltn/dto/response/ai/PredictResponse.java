package vn.id.nonglam.kltn.kltn.dto.response.ai;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.UUID;

public record PredictResponse(
        UUID id, // Có thể null
        @JsonProperty("original_text")
        String originalText,
        String message,
        List<AspectResult> results
) {}