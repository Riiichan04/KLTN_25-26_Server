package vn.id.nonglam.kltn.kltn.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.id.nonglam.kltn.kltn.dto.response.search.SearchHotelResponse;
import vn.id.nonglam.kltn.kltn.services.SearchService;

import java.util.List;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {
    private final SearchService searchService;

    @GetMapping("/hotels")
    public ResponseEntity<Page<SearchHotelResponse>> searchHotels(
            @RequestParam(required = false) String keyWord,
            @RequestParam(required = false) List<Double> extentAddress,
            @RequestParam int type,
            @RequestParam(required = false, defaultValue = "0") Integer minRating,
            @RequestParam(required = false, defaultValue = "0.0") Double minPrice,
            @RequestParam(required = false, defaultValue = Double.MAX_VALUE+"") Double maxPrice,
            @RequestParam Pageable pageable

            ) {
        return ResponseEntity.ok(searchService.searchHotels(keyWord, extentAddress,
                type, minRating, minPrice, maxPrice, pageable));
    }
}
