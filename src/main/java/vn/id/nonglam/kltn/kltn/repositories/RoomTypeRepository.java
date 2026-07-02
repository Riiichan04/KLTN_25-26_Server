package vn.id.nonglam.kltn.kltn.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.id.nonglam.kltn.kltn.models.hotel.RoomType;

import java.util.List;
import java.util.UUID;

@Repository
public interface RoomTypeRepository extends JpaRepository<RoomType, UUID> {
    @EntityGraph(attributePaths = {"roomDetails"})
    List<RoomType> findByHotel_IdAndIsActiveTrue(UUID hotelId);
}
