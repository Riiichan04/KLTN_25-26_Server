package vn.id.nonglam.kltn.kltn.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import vn.id.nonglam.kltn.kltn.common.enums.CommentSentiment;
import vn.id.nonglam.kltn.kltn.dto.request.comments.SentimentRequest;
import vn.id.nonglam.kltn.kltn.dto.request.comments.UpdateCommentRequest;
import vn.id.nonglam.kltn.kltn.dto.response.comments.CommentResponse;
import vn.id.nonglam.kltn.kltn.dto.request.comments.InsertCommentRequest;
import vn.id.nonglam.kltn.kltn.dto.response.comments.SentimentResponse;
import vn.id.nonglam.kltn.kltn.dto.response.common.ServiceResponse;
import vn.id.nonglam.kltn.kltn.models.hotel.Comment;
import vn.id.nonglam.kltn.kltn.repositories.CommentRepository;
import vn.id.nonglam.kltn.kltn.repositories.UserRepository;

import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final RestClient restClient;

    @Value("${app.model-server-url}")
    private String modelServerUrl;
    private final static String PATH_URL = "/comments/sentiment/";

    @Autowired
    public CommentService(CommentRepository commentRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setReadTimeout(5000);
        this.restClient = RestClient.builder()
                .requestFactory(factory)
                .build();
    }

    public Page<CommentResponse> getCommentsByHotel(UUID hotelId, Pageable pageable) {
        Page<Comment> listComment = commentRepository.findByHotelId(hotelId, pageable);
        return listComment.map(this::mapToCommentResponse);
    }

    @Transactional
    public ServiceResponse insertComments(InsertCommentRequest input) {
        try {
            Comment comment = new Comment();
            //FIXME: Get hotel here
//        comment.setHotel();
            comment.setUser(userRepository.findUserById(input.userId()));
            comment.setContent(input.content());
            comment.setRating(input.rating());
            comment.setParentId(input.parentId());
            SentimentResponse sentimentResponse = this.getCommentSentiment(input.content());
            comment.setSentiment(sentimentResponse.sentiment());
            commentRepository.save(comment);
            return new ServiceResponse(true, "Upload comment success");
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ServiceResponse(false, "Failed when upload comment");
        }
    }

    @Transactional
    public ServiceResponse updateComment(UUID commentId, UpdateCommentRequest input) {
        try {
            Comment comment = commentRepository.findCommentById(commentId);
            comment.setContent(input.content());
            SentimentResponse sentimentResponse = this.getCommentSentiment(input.content());
            comment.setSentiment(sentimentResponse.sentiment());
            commentRepository.save(comment);
            return new ServiceResponse(true, "Update comment success");
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ServiceResponse(false, "Failed when update comment");
        }
    }

    @Transactional
    public ServiceResponse deleteComment(UUID commentId) {
        try {
            String currentUsername = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();

            Comment comment = commentRepository.findCommentById(commentId);

            if (!comment.getUser().getUsername().equals(currentUsername)) {
                return new ServiceResponse(false, "Permission denied");
            }

            comment.setActive(false);
            commentRepository.save(comment);
            return new ServiceResponse(true, "Delete comment success");
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ServiceResponse(false, "Failed when delete comment");
        }
    }

    public Page<CommentResponse> getCommentByAuthor(UUID authorId, Pageable pageable) {
        Page<Comment> rawComment = commentRepository.findByUserId(authorId, pageable);
        return rawComment.map(this::mapToCommentResponse);
    }

    private CommentResponse mapToCommentResponse(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getRating(),
                //Get author here
                comment.getUpdatedAt()
        );
    }

    private SentimentResponse getCommentSentiment(String content) {
        try {
            return this.restClient.post()
                    .uri(modelServerUrl + PATH_URL)
                    .body(new SentimentRequest(content))
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (request, response) -> {
                        log.error("Server error: {}", response.getStatusCode());
                    })
                    .body(SentimentResponse.class);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new SentimentResponse(false, "Server error", CommentSentiment.NEUTRAL);
        }
    }
}
