package vn.id.nonglam.kltn.kltn.models.order;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.id.nonglam.kltn.kltn.models.hotel.RoomDetail;

import java.time.LocalDate;
import java.util.UUID;

@Table(name = "order_details")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_detail_id")
    private RoomDetail roomDetail;

    @Column
    private double actualPrice;

    @Column
    private LocalDate checkInDate;

    @Column
    private LocalDate checkOutDate;
}
