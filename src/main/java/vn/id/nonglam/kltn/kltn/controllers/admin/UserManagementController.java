package vn.id.nonglam.kltn.kltn.controllers.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.id.nonglam.kltn.kltn.dto.request.admin.AddUserRequest;
import vn.id.nonglam.kltn.kltn.dto.request.admin.UpdateUserRequest;
import vn.id.nonglam.kltn.kltn.dto.response.admin.AdminUserResponse;
import vn.id.nonglam.kltn.kltn.services.admin.AdminUserService;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class UserManagementController {
    private final AdminUserService adminUserService;

    @GetMapping("/get")
    public ResponseEntity<Page<AdminUserResponse>> getUsers(@RequestParam(required = false, defaultValue = "") String keyword,
                                                            @PageableDefault(
                                                                  size = 10,
                                                                  sort = "createdAt",
                                                                  direction = Sort.Direction.DESC
                                                          ) Pageable pageable) {
        return ResponseEntity.ok(adminUserService.getUsers(keyword, pageable));
    }

    @PostMapping("/add")
    public ResponseEntity<AdminUserResponse> addUser(@RequestBody AddUserRequest addUserRequest) {
        return ResponseEntity.ok(adminUserService.addUser(addUserRequest));
    }

    @PutMapping("/update")
    public ResponseEntity<AdminUserResponse> updateUser(@RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(adminUserService.updateUser(request));
    }
}
