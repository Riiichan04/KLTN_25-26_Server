package vn.id.nonglam.kltn.kltn.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Hotel hotel;

    //OwnerID

    @Column(nullable = false)
    private String content;

    @Column(nullable = true)
    private UUID parentId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
