package vn.id.nonglam.kltn.kltn.models.hotel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import vn.id.nonglam.kltn.kltn.common.enums.HotelStatus;
import vn.id.nonglam.kltn.kltn.models.user.User;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Table(
        name = "hotels",
        uniqueConstraints = {
                @UniqueConstraint(name = "unique_address_hotel", columnNames = {"address_id"})
        }
)
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Hotel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    @Column(length = 5000)
    private String description;

    @Column
    private String thumbnail;

    @OneToOne
    @JoinColumn(name = "address_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Address address;

    @OneToMany(mappedBy = "hotel", fetch = FetchType.LAZY)
    @JsonIgnoreProperties("hotel")
    private Set<RoomType> roomTypes;

    @OneToMany(mappedBy = "hotel", fetch = FetchType.LAZY)
    @JsonIgnoreProperties("hotel")
    private Set<HotelImage> images;

    @Column(nullable = true)
    private String hotline;

    @ManyToMany
    @JoinTable(
            name = "hotel_utilities_details",
            joinColumns = @JoinColumn(name = "hotel_id"),
            inverseJoinColumns = @JoinColumn(name = "hotel_utility_id")
    )
    private Set<HotelUtility> utilities;

    @OneToMany(mappedBy = "hotel", fetch = FetchType.LAZY)
    @JsonIgnoreProperties("hotel")
    private Set<Comment> comments;

    @OneToMany(mappedBy = "hotel", fetch = FetchType.LAZY)
    @JsonIgnoreProperties("hotel")
    private Set<HotelRegulation> regulations;

    @Column
    private int viewCount;

    @Column
    private boolean isActive;

    @Column
    private HotelStatus status;

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
