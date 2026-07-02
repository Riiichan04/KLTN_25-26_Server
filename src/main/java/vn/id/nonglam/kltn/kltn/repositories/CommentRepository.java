package vn.id.nonglam.kltn.kltn.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.id.nonglam.kltn.kltn.models.hotel.Comment;

import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {
    Page<Comment> findByHotelId(UUID hotelId, Pageable pageable);
    Page<Comment> findByUserId(UUID userId, Pageable pageable);
    Comment findCommentById(UUID id);
    int countByHotelIdAndIsActiveTrue(UUID hotelId);
    @Query("""
        SELECT round(coalesce(avg(c.rating), 0.0) , 2) FROM Comment c
        WHERE c.hotel.id = :hotelId AND c.isActive = true
    """)
    double avgRatingByHotelId(UUID hotelId);
}
