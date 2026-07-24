package vn.id.nonglam.kltn.kltn.dto.response.ai;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AspectResult(
        String aspect,
        String sentiment,
        Float probability,
        Float entropy,
        Float attention,
        @JsonProperty("opinion_word")
        String opinionWord
) {}