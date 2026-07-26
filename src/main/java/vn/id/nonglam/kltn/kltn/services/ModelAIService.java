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

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ModelAIService {
    private final RestTemplate restTemplate;
    private final CommentService commentService;
    private final CommentReviewAspectRepository reviewAspectRepository;
    private final AiABSAProperties properties;

    private PredictResponse predictComment(Comment comment) {
        String endpoint = properties.getBaseUrl() + "/predict";
        try {
            PredictResponse response = restTemplate.postForObject(endpoint, comment, PredictResponse.class);
            log.info("predict comment response: {}", response);
            return response;
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    @Transactional
    public List<CommentReviewAspect> predictAndSaveListAspectForComment(Comment comment) {
        PredictResponse predictComment = predictComment(comment);
        if (predictComment == null) {
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
