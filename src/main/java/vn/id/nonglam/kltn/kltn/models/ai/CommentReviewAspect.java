package vn.id.nonglam.kltn.kltn.models.ai;

import jakarta.persistence.*;
import lombok.*;
import vn.id.nonglam.kltn.kltn.models.hotel.Comment;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "comments_review_aspects")
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentReviewAspect {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    private String aspect;
    private String sentiment;
    private Float probability;
    private Float entropy;
    private Float attention;
    private String opinionWord;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}
