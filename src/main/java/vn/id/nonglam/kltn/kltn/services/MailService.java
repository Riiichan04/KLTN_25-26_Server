package vn.id.nonglam.kltn.kltn.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.math.BigDecimal;
import java.util.Locale;

@Service
@Slf4j
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${app.mail.from-address}")
    private String fromAddress;

    @Value("${app.mail.from-name}")
    private String fromName;

    @Async
    public void sendOtpEmail(String to, String otp) {
        try {
            Context context = new Context();
            context.setVariable("email", to);
            context.setVariable("otp", otp);
            context.setVariable("expireTime", 5);

            String htmlContent = templateEngine.process("email/otp-verification", context);

            sendHtmlMail(to, "HomeBook - OTP Verification", htmlContent);

            log.info("Email sent successfully to {}", to);
        } catch (Exception e) {
            log.error("CRITICAL: Could not send email to {}. Error: {}", to, e.getMessage());
        }
    }

    @Async
    public void sendInvoiceEmail(String to, String ownerName, String billingMonth, BigDecimal amount, String dueDate, String paymentUrl) {
        try {
            Context context = new Context();
            context.setVariable("ownerName", ownerName);
            context.setVariable("billingMonth", billingMonth);

            java.text.NumberFormat format = java.text.NumberFormat.getInstance(new Locale("vi", "VN"));
            context.setVariable("amount", format.format(amount));

            context.setVariable("dueDate", dueDate);
            context.setVariable("paymentUrl", paymentUrl);

            String htmlContent = templateEngine.process("email/invoice-notification", context);

            sendHtmlMail(to, "HomeBook - Hóa Đơn Phí Nền Tảng Tháng " + billingMonth, htmlContent);

            log.info("Invoice email sent successfully to {}", to);
        } catch (Exception e) {
            log.error("CRITICAL: Could not send invoice email to {}. Error: {}", to, e.getMessage());
        }
    }

    @Async
    public void sendAccountSuspendedEmail(String to, String ownerName, String billingMonth, BigDecimal amount, String paymentUrl) {
        try {
            Context context = new Context();
            context.setVariable("ownerName", ownerName);
            context.setVariable("billingMonth", billingMonth);

            java.text.NumberFormat format = java.text.NumberFormat.getInstance(new java.util.Locale("vi", "VN"));
            context.setVariable("amount", format.format(amount));

            context.setVariable("paymentUrl", paymentUrl);

            String htmlContent = templateEngine.process("email/account-suspended", context);

            sendHtmlMail(to, "Khẩn cấp: Tài khoản HomeBook của bạn đã bị tạm khóa", htmlContent);

            log.info("Suspension email sent successfully to {}", to);
        } catch (Exception e) {
            log.error("CRITICAL: Could not send suspension email to {}. Error: {}", to, e.getMessage());
        }
    }

    private void sendHtmlMail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    @Async
    public void sendNewBookingNoticeEmail(String to, String ownerName, String customerName, String orderId,
                                          java.time.LocalDateTime checkInDate, java.time.LocalDateTime checkOutDate,
                                          BigDecimal totalAmount, String manageOrderUrl) {
        try {
            Context context = new Context();
            context.setVariable("ownerName", ownerName);
            context.setVariable("customerName", customerName);
            context.setVariable("orderId", orderId);

            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
            context.setVariable("checkInDate", checkInDate.format(formatter));
            context.setVariable("checkOutDate", checkOutDate.format(formatter));

            java.text.NumberFormat format = java.text.NumberFormat.getInstance(new Locale("vi", "VN"));
            context.setVariable("totalAmount", format.format(totalAmount));

            context.setVariable("manageOrderUrl", manageOrderUrl);

            String htmlContent = templateEngine.process("email/new-booking-notice", context);
            sendHtmlMail(to, "HomeBook - Bạn có đơn đặt phòng mới", htmlContent);
            log.info("New booking notice sent to owner: {}", to);
        } catch (Exception e) {
            log.error("CRITICAL: Could not send new booking notice to {}. Error: {}", to, e.getMessage());
        }
    }

    @Async
    public void sendBookingStatusEmail(String to, String customerName, String hotelName, String orderId,
                                       String statusText, String statusColor, String note, String orderDetailUrl) {
        try {
            Context context = new Context();
            context.setVariable("customerName", customerName);
            context.setVariable("hotelName", hotelName);
            context.setVariable("orderId", orderId);
            context.setVariable("statusText", statusText);
            context.setVariable("statusColor", statusColor);
            context.setVariable("note", note);
            context.setVariable("orderDetailUrl", orderDetailUrl);

            String htmlContent = templateEngine.process("email/booking-status", context);
            sendHtmlMail(to, "HomeBook - Cập nhật trạng thái đơn đặt phòng #" + orderId, htmlContent);
            log.info("Booking status update sent to customer: {}", to);
        } catch (Exception e) {
            log.error("CRITICAL: Could not send booking status to {}. Error: {}", to, e.getMessage());
        }
    }

    @Async
    public void sendPaymentExpiredEmail(String to, String customerName, String hotelName, String orderId,
                                        BigDecimal amount, String rebookUrl) {
        try {
            Context context = new Context();
            context.setVariable("customerName", customerName);
            context.setVariable("hotelName", hotelName);
            context.setVariable("orderId", orderId);

            java.text.NumberFormat format = java.text.NumberFormat.getInstance(new Locale("vi", "VN"));
            context.setVariable("amount", format.format(amount));

            context.setVariable("rebookUrl", rebookUrl);

            String htmlContent = templateEngine.process("email/payment-expired", context);
            sendHtmlMail(to, "HomeBook - Đơn đặt phòng của bạn đã bị hủy do quá hạn thanh toán", htmlContent);
            log.info("Payment expired email sent to customer: {}", to);
        } catch (Exception e) {
            log.error("CRITICAL: Could not send payment expired email to {}. Error: {}", to, e.getMessage());
        }
    }
}
