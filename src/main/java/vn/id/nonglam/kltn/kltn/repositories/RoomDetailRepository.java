package vn.id.nonglam.kltn.kltn.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.id.nonglam.kltn.kltn.models.hotel.RoomDetail;

import java.util.UUID;

@Repository
public interface RoomDetailRepository extends JpaRepository<RoomDetail, UUID> {

}
