package vn.id.nonglam.kltn.kltn.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.id.nonglam.kltn.kltn.models.hotel.HotelUtility;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface HotelUtilityRepository extends JpaRepository<HotelUtility, UUID> {

    Optional<HotelUtility> findByName(String name);
    boolean existsByName(String name);
}