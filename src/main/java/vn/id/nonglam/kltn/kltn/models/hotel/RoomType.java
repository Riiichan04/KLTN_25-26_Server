package vn.id.nonglam.kltn.kltn.models.hotel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Table(name = "room_types")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoomType {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column
    private String name;

    @Column(length = 5000)
    private String description;

    @Column
    private int capacity;

    @Column
    private BigDecimal price;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "room_utilities_details",
            joinColumns = @JoinColumn(name = "room_type_id"),
            inverseJoinColumns = @JoinColumn(name = "room_utility_id")
    )
    private Set<RoomUtility> utilities;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name  = "hotel_id")
    private Hotel hotel;

    @OneToMany(mappedBy = "roomType", fetch = FetchType.LAZY)
    @JsonIgnoreProperties("roomType")
    private Set<RoomTypeImage> images;

    @OneToMany(mappedBy = "roomType", fetch = FetchType.LAZY)
    @JsonIgnoreProperties("roomType")
    private List<RoomDetail> roomDetails;

    @Column
    private double depositedPercent;

    @Column
    private boolean isActive;

    @Column
    private LocalDate createdAt;

    @Column
    private LocalDate updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDate.now();
        this.updatedAt = LocalDate.now();
    }
}
