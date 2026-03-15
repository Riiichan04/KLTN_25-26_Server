package vn.id.nonglam.kltn.kltn.models.hotel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.id.nonglam.kltn.kltn.models.order.OrderDetail;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Table(name = "room_details")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoomDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column
    private String roomCode;

    @ManyToOne
    @JoinColumn(name = "room_type_id")
    private RoomType roomType;

    @OneToMany(mappedBy = "roomDetail", fetch = FetchType.LAZY)
    @JsonIgnoreProperties("roomDetail")
    private List<OrderDetail> orderDetails;

    @Column
    private boolean isActive;

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

}
