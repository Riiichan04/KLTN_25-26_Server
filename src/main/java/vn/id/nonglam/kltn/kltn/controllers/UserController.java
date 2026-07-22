package vn.id.nonglam.kltn.kltn.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.id.nonglam.kltn.kltn.dto.request.auth.UpdateRequest;
import vn.id.nonglam.kltn.kltn.dto.response.auth.AuthDto;
import vn.id.nonglam.kltn.kltn.services.UserService;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;


    @PutMapping("/update")
    public ResponseEntity<AuthDto> update(@RequestBody UpdateRequest updateRequest) {
        AuthDto response = this.userService.update(updateRequest);
        return ResponseEntity.ok(response);
    }
}
