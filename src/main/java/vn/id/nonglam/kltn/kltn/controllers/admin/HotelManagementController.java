package vn.id.nonglam.kltn.kltn.controllers.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.id.nonglam.kltn.kltn.dto.request.admin.ChangeActiveHotelRequest;
import vn.id.nonglam.kltn.kltn.dto.request.admin.ChangeOwnerHotelRequest;
import vn.id.nonglam.kltn.kltn.dto.request.admin.UpdateHotelAdminRequest;
import vn.id.nonglam.kltn.kltn.dto.response.admin.AdminHotelResponse;
import vn.id.nonglam.kltn.kltn.dto.response.admin.ChangeActiveHotelResponse;
import vn.id.nonglam.kltn.kltn.dto.response.admin.GetAdminSnapshotHotelResponse;
import vn.id.nonglam.kltn.kltn.services.admin.AdminHotelService;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/hotels")
@RequiredArgsConstructor
public class HotelManagementController {
    private final AdminHotelService adminHotelService;

    @GetMapping("/get")
    public ResponseEntity<Page<GetAdminSnapshotHotelResponse>> getSnapshotHotels(Pageable pageable) {
        return ResponseEntity.ok(adminHotelService.getHotels(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminHotelResponse> getHotelById(@PathVariable UUID id) {
        return ResponseEntity.ok(adminHotelService.getHotelById(id));
    }

    @PatchMapping("/status")
    public ResponseEntity<ChangeActiveHotelResponse> changeActive(
            @RequestBody ChangeActiveHotelRequest request) {
        return ResponseEntity.ok(adminHotelService.changeActive(request));
    }

    @PatchMapping("/owner")
    public ResponseEntity<AdminHotelResponse.OwnerResponse> changeOwner(
            @RequestBody ChangeOwnerHotelRequest request) {
        return ResponseEntity.ok(adminHotelService.changeOwner(request));
    }

    @GetMapping("/utilities")
    public ResponseEntity<List<AdminHotelResponse.HotelUtilityResponse>> getHotelUtilities() {
        return ResponseEntity.ok(adminHotelService.getHotelUtilities());
    }

    @PutMapping("/update-hotel")
    public ResponseEntity<AdminHotelResponse> updateHotelInfo(@RequestBody UpdateHotelAdminRequest request) {
        return ResponseEntity.ok(adminHotelService.updateHotelInfo(request));
    }
}
