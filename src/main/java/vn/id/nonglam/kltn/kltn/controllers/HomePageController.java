package vn.id.nonglam.kltn.kltn.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.id.nonglam.kltn.kltn.dto.response.HomePageStatisticResponse;
import vn.id.nonglam.kltn.kltn.services.HotelService;

@RestController
@RequestMapping("/home")
@RequiredArgsConstructor
public class HomePageController {
    private final HotelService hotelService;

    public ResponseEntity<HomePageStatisticResponse> getHomePageData() {
        return ResponseEntity.ok(new HomePageStatisticResponse(hotelService.statisticProvincesByHotel()));
    }
}
