package vn.id.nonglam.kltn.kltn.controllers.owner;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.id.nonglam.kltn.kltn.common.enums.OrderStatus;
import vn.id.nonglam.kltn.kltn.dto.request.admin.ChangeOrderStatusRequest;
import vn.id.nonglam.kltn.kltn.dto.response.admin.AdminOrderResponse;
import vn.id.nonglam.kltn.kltn.services.admin.AdminOrderService;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/owner/orders")
@RequiredArgsConstructor
public class OwnerOrderManagementController {
    private final AdminOrderService adminOrderService;

    @GetMapping("/get")
    public ResponseEntity<Page<AdminOrderResponse>> getOrders(@RequestParam(required = false, defaultValue = "") String keyword,
                                                              @RequestParam(required = false) LocalDateTime startDate,
                                                              @RequestParam(required = false) LocalDateTime endDate,
                                                              @RequestParam(required = false) OrderStatus status,
                                                              @PageableDefault(
                                                                      size = 10,
                                                                      sort = "createdAt",
                                                                      direction = Sort.Direction.DESC
                                                              ) Pageable pageable) {
        return ResponseEntity.ok(adminOrderService.getOrders(keyword, status, startDate, endDate, pageable));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Boolean> changeStatusOrder(@PathVariable UUID id, @RequestBody ChangeOrderStatusRequest req) {
        return ResponseEntity.ok(adminOrderService.changeStatus(id, req));
    }

}
