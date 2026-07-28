package vn.id.nonglam.kltn.kltn.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.id.nonglam.kltn.kltn.models.ai.CommentReviewAspect;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommentReviewAspectRepository extends JpaRepository<CommentReviewAspect, UUID> {
    void deleteByComment_Id(UUID commentId);

    @Query(value = """
    select count(distinct c.comment.id) 
    from CommentReviewAspect c 
    where c.comment.isActive = true
""")
    long countCommentsWithPredictedAspects();

    @EntityGraph(attributePaths = {"comment"})
    List<CommentReviewAspect> findTop10ByComment_IsActiveTrueOrderByCreatedAtDesc();

    List<CommentReviewAspect> findByComment_Id(UUID commentId);
}
