package vn.id.nonglam.kltn.kltn.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.id.nonglam.kltn.kltn.models.Hotel;

import java.util.UUID;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, UUID> {
    Page<Hotel>  findByNameContainingIgnoreCase(String name, Pageable pageable);

    @Query("")
    Page<Hotel> findHotelsByExtentAddress(Double maxLongitude, Double maxLatitude, Double minLongitude, Double minLatitude, Pageable pageable);

    @Query("")
    Page<Hotel> findHotelsByCoordinates(Double longitude, Double latitude, int radius, Pageable pageable);
}
