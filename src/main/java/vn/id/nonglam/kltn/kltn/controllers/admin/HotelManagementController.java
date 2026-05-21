package vn.id.nonglam.kltn.kltn.controllers.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.id.nonglam.kltn.kltn.dto.request.admin.ChangeActiveHotelRequest;
import vn.id.nonglam.kltn.kltn.dto.request.admin.ChangeOwnerHotelRequest;
import vn.id.nonglam.kltn.kltn.dto.response.admin.AdminHotelResponse;
import vn.id.nonglam.kltn.kltn.dto.response.admin.ChangeActiveHotelResponse;
import vn.id.nonglam.kltn.kltn.dto.response.admin.GetSnapshotHotelResponse;
import vn.id.nonglam.kltn.kltn.services.admin.AdminHotelService;

import org.springframework.data.domain.Pageable;

import java.util.UUID;

@RestController
@RequestMapping("/admin/hotel-management")
@RequiredArgsConstructor
public class HotelManagementController {
    private final AdminHotelService adminHotelService;

    @GetMapping("/snapshot-hotels")
    public ResponseEntity<Page<GetSnapshotHotelResponse>> getSnapshotHotels(Pageable pageable) {
        return ResponseEntity.ok(adminHotelService.getHotels(pageable));
    }

    @GetMapping("get-hotel-by-id")
    public ResponseEntity<AdminHotelResponse> getHotelById(@RequestParam UUID id) {
        return ResponseEntity.ok(adminHotelService.getHotelById(id));
    }

    @PostMapping("/change-active")
    public ResponseEntity<ChangeActiveHotelResponse> changeActive(@RequestBody ChangeActiveHotelRequest request) {
        return ResponseEntity.ok(adminHotelService.changActive(request));
    }

    @PostMapping("/change-owner")
    public ResponseEntity<AdminHotelResponse.OwnerResponse> changeOwner(@RequestBody ChangeOwnerHotelRequest request) {
        return ResponseEntity.ok(adminHotelService.changeOwner(request));
    }

}
