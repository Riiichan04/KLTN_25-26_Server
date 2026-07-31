package vn.id.nonglam.kltn.kltn.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import vn.id.nonglam.kltn.kltn.config.AiABSAProperties;
import vn.id.nonglam.kltn.kltn.dto.response.ai.PredictResponse;
import vn.id.nonglam.kltn.kltn.models.ai.CommentReviewAspect;
import vn.id.nonglam.kltn.kltn.models.hotel.Comment;
import vn.id.nonglam.kltn.kltn.repositories.CommentReviewAspectRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ModelAIService {
    private final RestTemplate restTemplate;
    private final CommentReviewAspectRepository reviewAspectRepository;
    private final AiABSAProperties properties;

    private PredictResponse predictComment(Comment comment) {
        String endpoint = properties.getBaseUrl() + "/predict";
        try {
            // Đóng gói dữ liệu thành JSON object: {"text": "nội dung"}
            Map<String, String> requestPayload = new HashMap<>();
            requestPayload.put("text", comment.getContent());

            // Truyền requestPayload thay vì object comment
            PredictResponse response = restTemplate.postForObject(endpoint, requestPayload, PredictResponse.class);
            log.info("predict comment response: {}", response);
            return response;
        } catch (Exception e) {
            log.error("AI Model Error: {}", e.getMessage());
            return null;
        }
    }

    @Transactional
    public List<CommentReviewAspect> predictAndSaveListAspectForComment(Comment comment) {
        PredictResponse predictComment = predictComment(comment);

        // Bổ sung kiểm tra predictComment.results() để tránh lỗi Null Pointer
        if (predictComment == null || predictComment.results() == null) {
            return null;
        }

        List<CommentReviewAspect> listAspect = predictComment.results().stream().map(asp -> {
            CommentReviewAspect resObj = new CommentReviewAspect();
            resObj.setAspect(asp.aspect());
            resObj.setAttention(asp.attention());
            resObj.setEntropy(asp.entropy());
            resObj.setComment(comment);
            resObj.setOpinionWord(asp.opinionWord());
            resObj.setProbability(asp.probability());
            resObj.setSentiment(asp.sentiment());
            resObj.setCreatedAt(comment.getCreatedAt());
            resObj.setUpdatedAt(comment.getUpdatedAt());
            return resObj;
        }).toList();

        return reviewAspectRepository.saveAll(listAspect);
    }
}
