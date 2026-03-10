package vn.id.nonglam.kltn.kltn.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Table(name = "hotel_orders")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HotelOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


}
