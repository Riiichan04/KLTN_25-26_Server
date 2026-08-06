package vn.id.nonglam.kltn.kltn.controllers.owner;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.id.nonglam.kltn.kltn.dto.request.admin.*;
import vn.id.nonglam.kltn.kltn.dto.response.admin.AdminCommentResponse;
import vn.id.nonglam.kltn.kltn.dto.response.admin.AdminHotelResponse;
import vn.id.nonglam.kltn.kltn.dto.response.admin.ChangeActiveHotelResponse;
import vn.id.nonglam.kltn.kltn.dto.response.admin.ChangeActiveRoomTypeResponse;
import vn.id.nonglam.kltn.kltn.dto.response.admin.GetAdminSnapshotHotelResponse;
import vn.id.nonglam.kltn.kltn.services.admin.AdminHotelService;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/owner/hotels")
@RequiredArgsConstructor
public class OwnerHotelManagementController {
    private final AdminHotelService adminHotelService;

    @PostMapping("/create")
    public ResponseEntity<Boolean> createHotel(@RequestBody AddHotelRequest request) {
        return ResponseEntity.ok(adminHotelService.createHotel(request));
    }

    @GetMapping("/get")
    public ResponseEntity<Page<GetAdminSnapshotHotelResponse>> getSnapshotHotels(@RequestParam(required = false, defaultValue = "") String keyword,
                                                                                 @PageableDefault(
                                                                                         size = 10,
                                                                                         sort = "createdAt",
                                                                                         direction = Sort.Direction.DESC
                                                                                 ) Pageable pageable) {
        return ResponseEntity.ok(adminHotelService.getHotels(keyword, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminHotelResponse> getHotelById(@PathVariable UUID id) {
        return ResponseEntity.ok(adminHotelService.getHotelById(id));
    }

    @PatchMapping("/active")
    public ResponseEntity<ChangeActiveHotelResponse> changeActive(
            @RequestBody ChangeActiveHotelRequest request) {
        return ResponseEntity.ok(adminHotelService.changeActive(request));
    }

    @GetMapping("/utilities")
    public ResponseEntity<List<AdminHotelResponse.HotelUtilityResponse>> getHotelUtilities() {
        return ResponseEntity.ok(adminHotelService.getHotelUtilities());
    }

    @PutMapping("/update-hotel")
    public ResponseEntity<AdminHotelResponse> updateHotelInfo(@RequestBody UpdateHotelAdminRequest request) {
        return ResponseEntity.ok(adminHotelService.updateHotelInfo(request));
    }

    @GetMapping("/room-utilities")
    public ResponseEntity<List<AdminHotelResponse.RoomUtilityResponse>> getHotelRoomUtilities() {
        return ResponseEntity.ok(adminHotelService.getRoomUtilities());
    }

    @PutMapping("/update-room-type")
    public ResponseEntity<AdminHotelResponse.RoomTypeResponse> updateRoomType(@RequestBody UpdateRoomTypeAdminRequest request) {
        return ResponseEntity.ok(adminHotelService.updateRoomType(request));
    }

    @PatchMapping("/active-room-type")
    public ResponseEntity<ChangeActiveRoomTypeResponse> changeActiveRoomType(@RequestBody ChangeActiveRoomTypeRequest request) {
        return ResponseEntity.ok(adminHotelService.changeActiveRoomType(request));
    }

    @GetMapping("/comments/{id}")
    public ResponseEntity<Page<AdminCommentResponse>> getCommentsByHotelId(@PathVariable UUID id, @PageableDefault(
            size = 10,
            sort = "createdAt",
            direction = Sort.Direction.DESC
    ) Pageable pageable) {
        return ResponseEntity.ok(adminHotelService.getCommentsByHotelId(id, pageable));
    }
}
