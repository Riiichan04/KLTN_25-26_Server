package vn.id.nonglam.kltn.kltn.models.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Table(name = "user_payment_detail")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserPaymentDetail {
    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    User user;
    
    @Column(nullable = true)
    String bankAccount;
    @Column(nullable = false)
    String bankCode;
    @Column(nullable = false)
    String ownerName;
}
