package vn.id.nonglam.kltn.kltn.controllers.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.id.nonglam.kltn.kltn.dto.request.admin.AddUserRequest;
import vn.id.nonglam.kltn.kltn.dto.request.admin.ChangeUserActiveRequest;
import vn.id.nonglam.kltn.kltn.dto.request.admin.ChangeUserRoleRequest;
import vn.id.nonglam.kltn.kltn.dto.response.admin.GetUserResponse;
import vn.id.nonglam.kltn.kltn.services.admin.AdminUserService;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserManagementController {
    private final AdminUserService adminUserService;

    @GetMapping("/get-users-list")
    public ResponseEntity<Page<GetUserResponse>> getUsers(Pageable pageable) {
        return ResponseEntity.ok(adminUserService.getUsers(pageable));
    }

    @PostMapping("/add")
    public ResponseEntity<GetUserResponse> addUser(@RequestBody AddUserRequest addUserRequest) {
        return ResponseEntity.ok(adminUserService.addUser(addUserRequest));
    }

    @PostMapping("/change-role")
    public ResponseEntity<Boolean> changeRole(@RequestBody ChangeUserRoleRequest changeUserRoleRequest) {
        return ResponseEntity.ok(adminUserService.changeRole(changeUserRoleRequest));
    }

    @PostMapping("/change-active")
    public ResponseEntity<Boolean> changeActive(@RequestBody ChangeUserActiveRequest changeUserActiveRequest) {
        return ResponseEntity.ok(adminUserService.changeActive(changeUserActiveRequest));
    }
}
