package vn.id.nonglam.kltn.kltn.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.id.nonglam.kltn.kltn.dto.request.auth.LoginRequest;
import vn.id.nonglam.kltn.kltn.dto.request.auth.RegisterRequest;
import vn.id.nonglam.kltn.kltn.dto.response.auth.AuthResponse;
import vn.id.nonglam.kltn.kltn.services.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private vn.id.nonglam.kltn.kltn.services.AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        AuthResponse response = this.authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest registerRequest) {
        AuthResponse response = this.authService.register(registerRequest);
        return ResponseEntity.ok(response);
    }
}
