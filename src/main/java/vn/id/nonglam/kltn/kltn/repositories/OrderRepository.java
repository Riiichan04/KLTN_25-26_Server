package vn.id.nonglam.kltn.kltn.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.id.nonglam.kltn.kltn.common.enums.OrderStatus;
import vn.id.nonglam.kltn.kltn.dto.request.order.OrderStatusCount;
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

    Page<Order> findByUser_IdAndOrderStatus(UUID userId, OrderStatus status, Pageable pageable);

    @Query("SELECT o.orderStatus AS status, COUNT(o) AS count FROM Order o WHERE o.user.id = :userId GROUP BY o.orderStatus")
    List<OrderStatusCount> countOrderStatusByUserId(@Param("userId") UUID userId);

    List<Order> findByUser_Id(UUID user_id);

    List<Order> findByUser_IdAndHotel_Id(UUID user_id, UUID hotel_id);

    @Query("""
        select o from Order o left join o.hotel h
        where (h is null or h.name ilike concat('%', :keyword, '%'))
        and (:startDate is null or o.createdAt >= :startDate)
        and (:endDate is null or o.createdAt <= :endDate)
    """)
    @EntityGraph(attributePaths = {"hotel", "hotel.owner", "user"})
    Page<Order> findAll(@Param("keyword") String keyword, @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate, Pageable pageable);

    @Modifying
    @Query("UPDATE Order o SET o.orderStatus = :status, o.updatedAt = :updatedAt WHERE o.id = :id")
    int updateStatusOnly(@Param("id") UUID id, @Param("status") OrderStatus status, @Param("updatedAt") LocalDateTime updatedAt);

    @Query("SELECT o FROM Order o WHERE " +
            "(o.orderStatus = vn.id.nonglam.kltn.kltn.common.enums.OrderStatus.CONFIRMED AND o.checkInDate <= :deadlineForConfirmed) " +
            "OR (o.orderStatus = vn.id.nonglam.kltn.kltn.common.enums.OrderStatus.PENDING AND o.checkInDate <= :deadlineForPending)")
    List<Order> findOrdersToAutoCancel(@Param("deadlineForConfirmed") LocalDateTime deadlineForConfirmed,
                                       @Param("deadlineForPending") LocalDateTime deadlineForPending);
}
