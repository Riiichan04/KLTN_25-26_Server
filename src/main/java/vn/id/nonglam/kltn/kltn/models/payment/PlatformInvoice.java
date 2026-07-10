package vn.id.nonglam.kltn.kltn.models.payment;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import vn.id.nonglam.kltn.kltn.common.enums.InvoiceStatus;
import vn.id.nonglam.kltn.kltn.models.user.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "platform_invoices")
@Getter
@Setter
public class PlatformInvoice {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private BigDecimal amount;

    private String billingName; // Example: "06/2026"

    private LocalDateTime createdAt;
    private LocalDateTime dueDate; // ( createdAt + 1w )

    @Enumerated(EnumType.STRING)
    private InvoiceStatus status;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        dueDate = createdAt.plusDays(7);
        status = InvoiceStatus.PENDING;
    }
}