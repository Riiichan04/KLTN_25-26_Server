package vn.id.nonglam.kltn.kltn.dto.response.ai;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record BatchPredictResponse(
        String message,
        @JsonProperty("total_processed")
        Integer totalProcessed,
        @JsonProperty("batch_results")
        List<PredictResponse> batchResults
) {}