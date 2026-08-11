package vn.id.nonglam.kltn.kltn.controllers.payment;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.id.nonglam.kltn.kltn.common.enums.OrderStatus;
import vn.id.nonglam.kltn.kltn.common.enums.InvoiceStatus; // Đảm bảo import đúng đường dẫn
import vn.id.nonglam.kltn.kltn.common.utils.VnpayUtil;
import vn.id.nonglam.kltn.kltn.config.VnpayConfig;
import vn.id.nonglam.kltn.kltn.models.hotel.Hotel;
import vn.id.nonglam.kltn.kltn.models.order.Order;
import vn.id.nonglam.kltn.kltn.models.payment.PlatformInvoice;
import vn.id.nonglam.kltn.kltn.models.user.User;
import vn.id.nonglam.kltn.kltn.repositories.HotelRepository;
import vn.id.nonglam.kltn.kltn.repositories.OrderRepository;
import vn.id.nonglam.kltn.kltn.repositories.PlatformInvoiceRepository;
import vn.id.nonglam.kltn.kltn.repositories.UserRepository;
import vn.id.nonglam.kltn.kltn.services.payment.VnpayService;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final VnpayService vnpayService;
    private final OrderRepository orderRepository;
    private final PlatformInvoiceRepository invoiceRepository;
    private final VnpayConfig vnpayConfig;
    private final UserRepository userRepository;

    @PostMapping("/create/{orderId}")
    public ResponseEntity<String> createPaymentUrl(@PathVariable UUID orderId, HttpServletRequest request) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            return ResponseEntity.badRequest().body("Order not found");
        }

        if (order.getOrderStatus() != OrderStatus.CONFIRMED) {
            return ResponseEntity.badRequest().body("Order have not confirmed, can't proceed");
        }

        String vnpayUrl = vnpayService.createPaymentUrl(order, request);
        return ResponseEntity.ok(vnpayUrl);
    }

    @PostMapping("/create-invoice/{invoiceId}")
    public ResponseEntity<String> createInvoicePaymentUrl(@PathVariable UUID invoiceId, HttpServletRequest request) {
        PlatformInvoice invoice = invoiceRepository.findById(invoiceId).orElse(null);
        if (invoice == null) {
            return ResponseEntity.badRequest().body("Invoice not found");
        }

        if (invoice.getStatus() == InvoiceStatus.PAID) {
            return ResponseEntity.badRequest().body("Invoice has already been paid");
        }

        String vnpayUrl = vnpayService.createInvoicePaymentUrl(invoice, request);
        return ResponseEntity.ok(vnpayUrl);
    }
}