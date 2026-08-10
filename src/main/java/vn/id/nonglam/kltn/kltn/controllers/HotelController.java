package vn.id.nonglam.kltn.kltn.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.id.nonglam.kltn.kltn.dto.response.hotel.HotelResponse;
import vn.id.nonglam.kltn.kltn.dto.response.search.CardHotelResponse;
import vn.id.nonglam.kltn.kltn.services.HotelService;

import java.util.UUID;

@RestController
@RequestMapping("/hotel")
@RequiredArgsConstructor
public class HotelController {
    private final HotelService hotelService;

    @GetMapping("/get-hotel-detail")
    public ResponseEntity<HotelResponse> getHotelDetail(@RequestParam UUID id) {
        System.out.println(id);
        return ResponseEntity.ok(hotelService.getHotelById(id));
    }

    @GetMapping("/get-snapshot")
    public ResponseEntity<CardHotelResponse> getHotelSnapshot(@RequestParam UUID id) {
        return ResponseEntity.ok(hotelService.getSnapshotHotel(id));
    }
}
