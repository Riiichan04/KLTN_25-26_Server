package vn.id.nonglam.kltn.kltn.schedulers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import vn.id.nonglam.kltn.kltn.common.enums.OrderStatus;
import vn.id.nonglam.kltn.kltn.models.order.Order;
import vn.id.nonglam.kltn.kltn.models.payment.PlatformInvoice;
import vn.id.nonglam.kltn.kltn.models.user.User;
import vn.id.nonglam.kltn.kltn.repositories.OrderRepository;
import vn.id.nonglam.kltn.kltn.repositories.PlatformInvoiceRepository;
import vn.id.nonglam.kltn.kltn.services.MailService;

@Component
@RequiredArgsConstructor
public class InvoiceScheduler {

    private final OrderRepository orderRepository;
    private final PlatformInvoiceRepository invoiceRepository;
    private final MailService mailService;

    // Giả sử sàn thu phí 10% doanh thu
    private static final BigDecimal PLATFORM_FEE_RATE = new BigDecimal("0.10");

    @Scheduled(cron = "0 0 0 1 * ?")
    public void generateMonthlyInvoices() {
        YearMonth lastMonth = YearMonth.now().minusMonths(1);
        LocalDateTime startOfLastMonth = lastMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfLastMonth = lastMonth.atEndOfMonth().atTime(23, 59, 59);

        List<Order> monthlyOrders = orderRepository.findAllCompletedOrdersInMonth(
                startOfLastMonth, endOfLastMonth, OrderStatus.COMPLETED
        );

        if (monthlyOrders.isEmpty()) return;

        Map<User, List<Order>> ordersGroupedByOwner = monthlyOrders.stream()
                .collect(Collectors.groupingBy(order -> order.getHotel().getOwner()));

        ordersGroupedByOwner.forEach((owner, ownerOrders) -> {

            BigDecimal totalFee = BigDecimal.ZERO;
            for (Order order : ownerOrders) {
                totalFee = totalFee.add(order.calculateTotalPlatformFee());
            }

            if (totalFee.compareTo(BigDecimal.ZERO) > 0) {
                PlatformInvoice invoice = new PlatformInvoice();
                invoice.setUser(owner);
                invoice.setAmount(totalFee);
                invoice.setBillingName(lastMonth.toString());

                invoiceRepository.save(invoice);

                String emailBody = String.format(
                        "Kính gửi %s,\n\nHóa đơn phí nền tảng tháng %s cho các khách sạn của bạn là %s VND.\nVui lòng thanh toán trước ngày %s.\n\nTrân trọng!",
                        owner.getUsername(),
                        lastMonth.toString(),
                        totalFee,

                        invoice.getDueDate() != null ? invoice.getDueDate().toLocalDate() : "7 ngày tới"
                );

            }
        });
    }
}