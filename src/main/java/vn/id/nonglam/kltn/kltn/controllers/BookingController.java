package vn.id.nonglam.kltn.kltn.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.id.nonglam.kltn.kltn.dto.OrderDTO;
import vn.id.nonglam.kltn.kltn.services.BookingService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/booking")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @GetMapping("/choose-room")
    public ResponseEntity<List<OrderDTO.RoomDetailValidResponse>> getRoomDetailsValid(@RequestParam UUID hotelId,
                                                                                      @RequestParam LocalDateTime startDate,
                                                                                      @RequestParam LocalDateTime endDate) {
        return ResponseEntity.ok(bookingService.getRoomDetailsValid(hotelId, startDate, endDate));
    }

}
