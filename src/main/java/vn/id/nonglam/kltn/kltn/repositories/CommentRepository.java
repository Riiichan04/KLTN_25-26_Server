package vn.id.nonglam.kltn.kltn.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import vn.id.nonglam.kltn.kltn.models.hotel.Comment;
import vn.id.nonglam.kltn.kltn.models.hotel.Hotel;

import java.util.UUID;

@Repository
public interface CommentRepository {
    Page<Comment> findByHotelId(UUID hotelId, Pageable pageable);
    Page<Comment> findByUserId(UUID userId, Pageable pageable);
}
