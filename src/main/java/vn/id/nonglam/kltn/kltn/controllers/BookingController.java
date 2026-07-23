package vn.id.nonglam.kltn.kltn.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.id.nonglam.kltn.kltn.dto.request.order.OrderRequest;
import vn.id.nonglam.kltn.kltn.dto.request.order.UpdateOrderStatusRequest;
import vn.id.nonglam.kltn.kltn.dto.response.order.OrderResponse;
import vn.id.nonglam.kltn.kltn.dto.response.order.UpdateOrderResponse;
import vn.id.nonglam.kltn.kltn.models.user.User;
import vn.id.nonglam.kltn.kltn.repositories.UserRepository;
import vn.id.nonglam.kltn.kltn.security.SecurityUtil;
import vn.id.nonglam.kltn.kltn.services.BookingService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/booking")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private final UserRepository userRepository;

    @GetMapping("/choose-room")
    public ResponseEntity<List<OrderResponse.RoomDetailValidResponse>> getRoomDetailsValid(@RequestParam UUID hotelId,
                                                                                           @RequestParam LocalDateTime startDate,
                                                                                           @RequestParam LocalDateTime endDate) {
        return ResponseEntity.ok(bookingService.getRoomDetailsValid(hotelId, startDate, endDate));
    }

    @PostMapping("/create-order")
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest request) {
        return ResponseEntity.ok(bookingService.createOrder(request));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<UpdateOrderResponse> cancelOrder(@PathVariable UUID id, @RequestBody UpdateOrderStatusRequest statusRequest) {
        User user = userRepository.findUserById(SecurityUtil.currentUserId().orElse(null));
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        UpdateOrderResponse result = bookingService.updateOrderStatus(id, statusRequest.status());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/get")
    public ResponseEntity<Page<HistoryOrderResponse>> getOrder(@RequestParam OrderStatus status, Pageable pageable) {
        return ResponseEntity.ok(bookingService.getHistoryOrders(status, pageable));
    }

    @GetMapping("/count")
    public ResponseEntity<List<OrderStatusCount>> countOrders() {
        return ResponseEntity.ok(bookingService.countOrders());
    }

    @GetMapping("/get/user/{id}")
    public ResponseEntity<List<UserOrderResponse>> getAllUserOrder(@PathVariable UUID id) {
        return ResponseEntity.ok(bookingService.getOrderByUserId(id));
    }
}
