package vn.id.nonglam.kltn.kltn.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.id.nonglam.kltn.kltn.models.hotel.RoomUtility;

import java.util.List;
import java.util.UUID;

public interface RoomUtilityRepository extends JpaRepository<RoomUtility, UUID> {
    List<RoomUtility> findAllByIsActive(boolean isActive);
}
