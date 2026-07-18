package vn.id.nonglam.kltn.kltn.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.id.nonglam.kltn.kltn.common.enums.OrderStatus;
import vn.id.nonglam.kltn.kltn.dto.request.order.OrderStatusCount;
import vn.id.nonglam.kltn.kltn.models.order.Order;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    Page<Order> findByUser_IdAndOrderStatus(UUID userId, OrderStatus status, Pageable pageable);

    @Query("SELECT o.orderStatus AS status, COUNT(o) AS count FROM Order o WHERE o.user.id = :userId GROUP BY o.orderStatus")
    List<OrderStatusCount> countOrderStatusByUserId(@Param("userId") UUID userId);
}
