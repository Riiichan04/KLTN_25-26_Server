package vn.id.nonglam.kltn.kltn.schedulers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.id.nonglam.kltn.kltn.models.order.Order;
import vn.id.nonglam.kltn.kltn.repositories.OrderRepository;
import vn.id.nonglam.kltn.kltn.services.BookingService;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderScheduler {
    private final OrderRepository orderRepository;
    private final BookingService bookingService;

    @Scheduled(cron = "0 0/15 * * * *")
    @Transactional
    public void autoCancelUnpaidOrders() {
        LocalDateTime now = LocalDateTime.now();
        // Điều kiện 1: Đơn CONFIRMED phải thanh toán trước 12 tiếng so với thời điểm check-in.
        // Nghĩa là nếu checkInDate <= (bây giờ + 12 tiếng) thì đơn đó vi phạm.
        LocalDateTime deadlineForConfirmed = now.plusHours(12);

        // Điều kiện 2: Quá thời gian check-in. Nếu đơn PENDING mà checkInDate <= bây giờ thì vi phạm.
        List<Order> expiredOrders = orderRepository.findOrdersToAutoCancel(deadlineForConfirmed, now);

        if (expiredOrders.isEmpty()) {
            log.info("[CRON-JOB] Không có đơn đặt phòng quá hạn");
            return;
        }

        int count = 0;
        for (Order order : expiredOrders) {
            try {
                bookingService.cancelExpiredPaymentOrder(order.getId());
                count++;
                log.info("[CRON-JOB] Đã tự động hủy đơn ID: {} (Check-in: {})", order.getId(), order.getCheckInDate());
            } catch (Exception e) {
                log.error("[CRON-JOB] Lỗi khi xử lý hủy đơn hàng ID: {} - Lỗi: {}", order.getId(), e.getMessage());
            }
        }

        log.info("[CRON-JOB] Đã tự động hủy {}/{} đơn hàng.", count, expiredOrders.size());
    }
}