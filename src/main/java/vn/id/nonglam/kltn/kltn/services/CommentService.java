package vn.id.nonglam.kltn.kltn.services;

import lombok.extern.slf4j.Slf4j;
import org.jdbi.v3.sqlobject.transaction.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.id.nonglam.kltn.kltn.dto.response.common.CommentResponse;
import vn.id.nonglam.kltn.kltn.dto.response.common.InsertCommentRequest;
import vn.id.nonglam.kltn.kltn.dto.response.common.ServiceResponse;
import vn.id.nonglam.kltn.kltn.models.hotel.Comment;
import vn.id.nonglam.kltn.kltn.repositories.CommentRepository;
import vn.id.nonglam.kltn.kltn.repositories.UserRepository;

import java.util.UUID;

@Service
@Slf4j
public class CommentService {
    CommentRepository commentRepository;
    UserRepository userRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public Page<CommentResponse> getComments(UUID hotelId, Pageable pageable) {
        Page<Comment> listComment = commentRepository.findByHotelId(hotelId, pageable);
        return listComment.map(this::mapToCommentResponse);
    }

    @Transaction
    public ServiceResponse insertComments(InsertCommentRequest input) {
       try {
           Comment comment = new Comment();
           //FIXME: Get hotel here
//        comment.setHotel();
           comment.setUser(userRepository.findUserById(input.userId()));
           comment.setContent(input.content());
           comment.setRating(input.rating());
           comment.setParentId(input.parentId());
           //Enum later
           return new ServiceResponse(true, "Upload comment success");
       }
       catch (Exception e) {
           log.error(e.getMessage());
           return new ServiceResponse(false, "Failed when upload comment");
       }
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
}
