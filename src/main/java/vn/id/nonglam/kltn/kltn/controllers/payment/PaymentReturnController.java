package vn.id.nonglam.kltn.kltn.controllers.payment;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.id.nonglam.kltn.kltn.services.payment.VnpayService;

import java.io.IOException;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentReturnController {
    private final VnpayService paymentService;

    @GetMapping("/vnpay-return")
    public void vnpayReturn(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int paymentStatus = paymentService.processVnpayReturn(request);
        System.out.println("payment status: " + paymentStatus);
        String vnp_TxnRef = request.getParameter("vnp_TxnRef");
        String entityId = "";

        if (vnp_TxnRef != null) {
            int lastDashIndex = vnp_TxnRef.lastIndexOf("-");
            String refWithoutTime = (lastDashIndex > 0) ? vnp_TxnRef.substring(0, lastDashIndex) : vnp_TxnRef;

            if (refWithoutTime.length() > 4) {
                entityId = refWithoutTime.substring(4);
            }
        }

        String frontendUrl = "http://localhost:3000/user/order";    //FIXME: Put this url into application.properties
        response.sendRedirect(frontendUrl);
    }
}