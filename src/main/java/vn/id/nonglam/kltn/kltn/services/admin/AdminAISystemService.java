package vn.id.nonglam.kltn.kltn.services.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.client.RestTemplate;
import vn.id.nonglam.kltn.kltn.config.AiABSAProperties;
import vn.id.nonglam.kltn.kltn.dto.request.ai.BatchPredictRequest;
import vn.id.nonglam.kltn.kltn.dto.request.ai.ReviewItem;
import vn.id.nonglam.kltn.kltn.dto.response.ai.BatchPredictResponse;
import vn.id.nonglam.kltn.kltn.dto.response.ai.CommentReviewAspectResponse;
import vn.id.nonglam.kltn.kltn.dto.response.ai.PredictResponse;
import vn.id.nonglam.kltn.kltn.models.ai.CommentReviewAspect;
import vn.id.nonglam.kltn.kltn.models.hotel.Comment;
import vn.id.nonglam.kltn.kltn.repositories.CommentRepository;
import vn.id.nonglam.kltn.kltn.repositories.CommentReviewAspectRepository;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminAISystemService {

    private final CommentRepository commentRepository;
    private final CommentReviewAspectRepository commentReviewAspectRepository;
    private final RestTemplate restTemplate;
    private final TransactionTemplate transactionTemplate;
    private final AiABSAProperties properties;
    private static final int BATCH_SIZE = 50;

    public Map<String, Object> batchAnalyze() {
        String url = properties.getBaseUrl() + "/batch-predict";

        Map<String, Object> result = new HashMap<>();
        int totalSuccessBatches = 0;
        int totalFailedBatches = 0;
        int totalCommentsProcessed = 0;

        List<Comment> allComments = commentRepository.findAllByIsActiveTrue();
        log.info("Bắt đầu phân tích {} đánh giá, mỗi lần gửi {} dòng", allComments.size(), BATCH_SIZE);

        for (int i = 0; i < allComments.size(); i += BATCH_SIZE) {
            int end = Math.min(allComments.size(), i + BATCH_SIZE);
            List<Comment> batchComments = allComments.subList(i, end);

            List<ReviewItem> reviewItems = new ArrayList<>();
            for (Comment c : batchComments) {
                reviewItems.add(new ReviewItem(c.getId(), c.getContent()));
            }
            BatchPredictRequest req = new BatchPredictRequest(reviewItems);

            try {
                log.info("Đang xử lý batch từ dòng {} đến {}", i, end);
                BatchPredictResponse res = restTemplate.postForObject(
                        url,
                        req,
                        BatchPredictResponse.class
                );

                if (res != null && res.batchResults() != null) {
                    saveResultsToDatabase(res);
                    totalCommentsProcessed += res.totalProcessed();
                    totalSuccessBatches++;
                }

            } catch (Exception e) {
                totalFailedBatches++;
                log.error("Lỗi khi phân tích batch từ {} đến {}: {}", i, end, e.getMessage());
            }
        }

        log.info("Hoàn thành quá trình phân tích hàng loạt.");

        result.put("totalComments", allComments.size());
        result.put("processedComments", totalCommentsProcessed);
        result.put("successBatches", totalSuccessBatches);
        result.put("failedBatches", totalFailedBatches);
        result.put("message", "Quá trình phân tích hoàn tất!");

        return result;
    }

    public void saveResultsToDatabase(BatchPredictResponse response) {
        transactionTemplate.executeWithoutResult(status -> {
            for (PredictResponse p : response.batchResults()) {
                if (p.results() != null && !p.results().isEmpty()) {
                    commentReviewAspectRepository.deleteByComment_Id(p.id());
                    Comment c = commentRepository.findByIdAndIsActiveTrue(p.id())
                            .orElseThrow(() -> new NoSuchElementException("Không tìm thấy comment " + p.id()));

                    List<CommentReviewAspect> aspects = p.results().stream().map(v -> CommentReviewAspect.builder()
                            .comment(c)
                            .aspect(v.aspect())
                            .entropy(v.entropy())
                            .attention(v.attention())
                            .sentiment(v.sentiment())
                            .probability(v.probability())
                            .opinionWord(v.opinionWord())
                            .build()).toList();

                    commentReviewAspectRepository.saveAll(aspects);
                }
            }
        });
    }

    @Transactional
    public Map<String, Object> getDashboard() {
        Map<String, Object> result = new HashMap<>();
        long countComments = commentRepository.countAllByIsActiveTrue();
        long countCommentsPredict = commentReviewAspectRepository.countCommentsWithPredictedAspects();
        List<CommentReviewAspectResponse> aspects = commentReviewAspectRepository.findTop10ByComment_IsActiveTrueOrderByCreatedAtDesc()
                .stream().map(c -> CommentReviewAspectResponse.builder()
                        .id(c.getId())
                        .aspect(c.getAspect())
                        .content(c.getComment().getContent())
                        .sentiment(c.getSentiment())
                        .opinionWord(c.getOpinionWord())
                        .createdAt(c.getCreatedAt())
                        .build()).toList();

        result.put("totalComments", countComments);
        result.put("commentsPredicted", countCommentsPredict);
        result.put("reviewAspectsNewest", aspects);
        return result;
    }
}