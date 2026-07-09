package vn.id.nonglam.kltn.kltn.models.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.id.nonglam.kltn.kltn.common.enums.Gender;
import vn.id.nonglam.kltn.kltn.common.enums.UserRole;
import vn.id.nonglam.kltn.kltn.common.enums.UserType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Table(name = "users")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "username", length = 50, unique = true, nullable = false)
    private String username;

    @Column(name = "password", length = 255, nullable = false)
    private String password;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "gender")
    private Gender gender;

    @Column(name = "role")
    private UserRole role;

    @Column(name = "active", nullable = false)
    private boolean isActive;

    @Column(name = "verified", nullable = false)
    private boolean isVerified;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "TEXT")
    private UserType userType;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "payment_detail")
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserPaymentDetail> paymentDetails;

    @PrePersist
    protected void onCreate() {
        // FIXME: add default avatar url later.
        avatarUrl = "";
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
}
