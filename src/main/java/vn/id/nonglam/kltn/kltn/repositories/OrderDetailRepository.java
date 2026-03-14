package vn.id.nonglam.kltn.kltn.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.id.nonglam.kltn.kltn.models.order.OrderDetail;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, UUID> {
    @Query("""
        SELECT (count(o) = 0) FROM OrderDetail o LEFT JOIN o.order order
        WHERE order.orderStatus != 'CANCELLED' AND o.roomDetail.id = :roomDetailId
        AND o.checkInDate < :endDate
        AND o.checkOutDate  > :startDate
    """)
    boolean checkValidRoomDetail(UUID roomDetailId, LocalDateTime startDate, LocalDateTime endDate);

}
