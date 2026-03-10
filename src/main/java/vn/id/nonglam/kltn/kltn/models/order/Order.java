package vn.id.nonglam.kltn.kltn.models.order;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.id.nonglam.kltn.kltn.common.enums.OrderStatus;
import vn.id.nonglam.kltn.kltn.common.enums.PaymentStatus;
import vn.id.nonglam.kltn.kltn.models.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Table(name = "orders")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToMany(mappedBy = "order")
    @JsonIgnoreProperties("order")
    private List<OrderDetail> orderDetails;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column
    private OrderStatus orderStatus;

    @Column
    private double totalPrice;

    @Column
    private String note;

    @Column
    private PaymentStatus paymentStatus;

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
}
