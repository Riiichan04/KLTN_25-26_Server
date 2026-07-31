package vn.id.nonglam.kltn.kltn.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import vn.id.nonglam.kltn.kltn.common.enums.CommentSentiment;
import vn.id.nonglam.kltn.kltn.common.enums.OrderStatus;
import vn.id.nonglam.kltn.kltn.common.utils.UserUtil;
import vn.id.nonglam.kltn.kltn.dto.request.comments.UpdateCommentRequest;
import vn.id.nonglam.kltn.kltn.dto.response.comments.CommentResponse;
import vn.id.nonglam.kltn.kltn.dto.request.comments.InsertCommentRequest;
import vn.id.nonglam.kltn.kltn.dto.response.comments.SentimentAspect;
import vn.id.nonglam.kltn.kltn.dto.response.comments.SentimentResponse;
import vn.id.nonglam.kltn.kltn.dto.response.common.ServiceResponse;
import vn.id.nonglam.kltn.kltn.models.ai.CommentReviewAspect;
import vn.id.nonglam.kltn.kltn.models.hotel.Comment;
import vn.id.nonglam.kltn.kltn.models.hotel.Hotel;
import vn.id.nonglam.kltn.kltn.repositories.*;
import vn.id.nonglam.kltn.kltn.security.SecurityUtil;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final HotelRepository hotelRepository;
    private final CommentReviewAspectRepository commentReviewAspectRepository;
    private final OrderRepository orderRepository;
    private final ModelAIService modelAIService;


    @Autowired
    public CommentService(CommentRepository commentRepository, UserRepository userRepository, HotelRepository hotelRepository, CommentReviewAspectRepository commentReviewAspectRepository, OrderRepository orderRepository, ModelAIService modelAIService) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.hotelRepository = hotelRepository;
        this.commentReviewAspectRepository = commentReviewAspectRepository;
        this.modelAIService = modelAIService;
        this.orderRepository = orderRepository;
    }

    public Page<CommentResponse> getCommentsByHotel(UUID hotelId, Pageable pageable) {
        Page<Comment> listComment = commentRepository.findByHotelId(hotelId, pageable);
        return listComment.map(this::mapToCommentResponse);
    }

    @Transactional
    public ServiceResponse insertComments(InsertCommentRequest input) {
        try {
            UUID userId = SecurityUtil.currentUserId().orElse(null);
            if (userId == null) return new ServiceResponse(false, "User not found");

            Hotel hotel = hotelRepository.getHotelById(input.hotelId());
            if (hotel == null) {
                return new ServiceResponse(false, "Hotel not found");
            }

            if (orderRepository
                    .findByUser_IdAndHotel_Id(userId, input.hotelId())
                    .stream()
                    .filter(order -> order
                            .getOrderStatus()
                            .equals(OrderStatus.COMPLETED)
                    ).findFirst()
                    .orElse(null) == null
            ) {
                return new ServiceResponse(false, "You have not complete any booking in this hotel");
            }

            Comment comment = new Comment();
            comment.setHotel(hotel);
            comment.setUser(userRepository.findUserById(input.userId()));
            comment.setContent(input.content());
            comment.setRating(input.rating());
            comment.setParentId(input.parentId());
            this.getCommentSentiment(comment);
//            SentimentResponse sentimentResponse = this.getCommentSentiment(input.content());
//            comment.setSentiment(sentimentResponse.sentiment());
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
            this.getCommentSentiment(comment);
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
        System.out.println(comment.getContent());
        List<SentimentAspect> listSentiments = commentReviewAspectRepository
                .findByComment_Id(comment.getId())
                .stream().map(aspect -> new SentimentAspect(
                        aspect.getAspect(),
                        aspect.getSentiment(),
                        aspect.getOpinionWord()
                )).toList();
        return new CommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getRating(),
                UserUtil.convertUserToUserDTO(comment.getUser()),
                listSentiments,
                comment.getUpdatedAt()
        );
    }

    private void getCommentSentiment(Comment comment) {
        try {
           modelAIService.predictAndSaveListAspectForComment(comment);
//            return this.restClient.post()
//                    .uri(modelServerUrl + PATH_URL)
//                    .body(new SentimentRequest(content))
//                    .retrieve()
//                    .onStatus(HttpStatusCode::isError, (request, response) -> {
//                        log.error("Server error: {}", response.getStatusCode());
//                    })
//                    .body(SentimentResponse.class);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
