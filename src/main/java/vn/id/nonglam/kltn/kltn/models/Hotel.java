package vn.id.nonglam.kltn.kltn.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import vn.id.nonglam.kltn.kltn.common.enums.HotelStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Hotel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;
    private String description;

    @OneToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Address address;

    @Column(nullable = true)
    private String hotline;

    //Price
    //Utilities
    //Room amount
    //Owner

    private int viewCount;

    private boolean isActive;
    private HotelStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
