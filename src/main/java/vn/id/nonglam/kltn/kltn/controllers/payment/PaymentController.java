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

    @GetMapping("/vnpay-ipn")
    public ResponseEntity<?> vnpayIpn(HttpServletRequest request) {
        Map<String, String> fields = new HashMap<>();
        for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements(); ) {
            String fieldName = params.nextElement();
            String fieldValue = request.getParameter(fieldName);
            if ((fieldName != null) && (!fieldName.isEmpty())) {
                fields.put(fieldName, fieldValue);
            }
        }

        String vnp_SecureHash = request.getParameter("vnp_SecureHash");
        fields.remove("vnp_SecureHashType");
        fields.remove("vnp_SecureHash");

        String signValue = VnpayUtil.hashAllFields(fields, vnpayConfig.getHashSecret());

        if (signValue.equals(vnp_SecureHash)) {
            String vnp_TxnRef = request.getParameter("vnp_TxnRef");
            String vnp_Amount = request.getParameter("vnp_Amount");
            String vnp_ResponseCode = request.getParameter("vnp_ResponseCode");

            try {
                int lastDashIndex = vnp_TxnRef.lastIndexOf("-");
                String refWithoutTime = (lastDashIndex > 0) ? vnp_TxnRef.substring(0, lastDashIndex) : vnp_TxnRef;

                if (refWithoutTime.startsWith("ORD-")) {
                    UUID orderId = UUID.fromString(refWithoutTime.substring(4));
                    Order order = orderRepository.findById(orderId).orElse(null);

                    if (order == null) return ResponseEntity.ok(Map.of("RspCode", "01", "Message", "Order not found"));

                    long amountInDb = order.calculateTotalAmount().multiply(new BigDecimal(100)).longValue();
                    if (amountInDb != Long.parseLong(vnp_Amount)) {
                        return ResponseEntity.ok(Map.of("RspCode", "04", "Message", "Invalid amount"));
                    }

                    if (order.getOrderStatus() != OrderStatus.CONFIRMED) {
                        return ResponseEntity.ok(Map.of("RspCode", "02", "Message", "Order already processed"));
                    }

                    if ("00".equals(vnp_ResponseCode)) {
                        order.setOrderStatus(OrderStatus.PAID);
                    } else {
                        order.setOrderStatus(OrderStatus.CONFIRMED);
                    }
                    orderRepository.save(order);
                } else if (refWithoutTime.startsWith("INV-")) {
                    UUID invoiceId = UUID.fromString(refWithoutTime.substring(4));
                    PlatformInvoice invoice = invoiceRepository.findById(invoiceId).orElse(null);

                    if (invoice == null)
                        return ResponseEntity.ok(Map.of("RspCode", "01", "Message", "Invoice not found"));

                    long amountInDb = invoice.getAmount().multiply(new BigDecimal(100)).longValue();
                    if (amountInDb != Long.parseLong(vnp_Amount)) {
                        return ResponseEntity.ok(Map.of("RspCode", "04", "Message", "Invalid amount"));
                    }

                    if (invoice.getStatus() == InvoiceStatus.PAID) {
                        return ResponseEntity.ok(Map.of("RspCode", "02", "Message", "Invoice already processed"));
                    }

                    if ("00".equals(vnp_ResponseCode)) {
                        invoice.setStatus(InvoiceStatus.PAID);
                        User user = invoice.getUser();
                        user.setActive(true);
                        userRepository.save(user);
                    }

                    invoiceRepository.save(invoice);
                }

                return ResponseEntity.ok(Map.of("RspCode", "00", "Message", "Confirm Success"));

            } catch (Exception e) {
                log.error("Error processing IPN: ", e);
                return ResponseEntity.ok(Map.of("RspCode", "99", "Message", "Unknown error"));
            }
        } else {
            return ResponseEntity.ok(Map.of("RspCode", "97", "Message", "Invalid Checksum"));
        }
    }
}