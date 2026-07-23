package vn.id.nonglam.kltn.kltn.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.id.nonglam.kltn.kltn.common.enums.OrderStatus;
import vn.id.nonglam.kltn.kltn.models.order.Order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    @Query("""
        SELECT DISTINCT o
        FROM Order o
        JOIN FETCH o.orderDetails
        JOIN FETCH o.hotel h
        JOIN FETCH h.owner
        WHERE o.createdAt >= :startDate
                AND o.createdAt <= :endDate
                AND o.orderStatus = :status
    """)
    List<Order> findAllCompletedOrdersInMonth(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("status") OrderStatus status
    );

    List<Order> findByUser_Id(UUID user_id);
}
