package vn.id.nonglam.kltn.kltn.controllers.payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.id.nonglam.kltn.kltn.services.payment.SepayPaymentService;

import java.util.Map;

@RestController
@RequestMapping("/payment/webhook")
@RequiredArgsConstructor
@Slf4j
public class WebhookController {

    private final SepayPaymentService sepayPaymentService;

    @PostMapping("/sepay")
    public ResponseEntity<Map<String, Boolean>> handleSepayWebhook(@RequestBody Map<String, Object> payload) {
        log.info("Nhận Webhook từ SePay: {}", payload);
        try {
            sepayPaymentService.handleWebhook(payload);
        } catch (Exception e) {
            log.error("Lỗi xử lý Webhook SePay: ", e);
            throw e;
        }

        return ResponseEntity.ok(Map.of("success", true));
    }
}