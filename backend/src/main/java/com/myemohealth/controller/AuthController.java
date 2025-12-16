package com.myemohealth.controller;

import com.myemohealth.dto.auth.AuthResponse;
import com.myemohealth.dto.auth.LoginRequest;
import com.myemohealth.dto.auth.RegisterRequest;
import com.myemohealth.service.AuthService;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for authentication endpoints
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * POST /api/auth/login
     * Authenticate user and return JWT tokens
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * POST /api/auth/register
     * Register a new patient
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * POST /api/auth/refresh
     * Refresh access token using refresh token
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshTokenRequest request) {
        try {
            AuthResponse response = authService.refreshToken(request.getRefreshToken());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * POST /api/auth/logout
     * Logout user (client should discard tokens)
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // In a stateless JWT system, logout is handled client-side
        // Optionally, add token to blacklist here
        return ResponseEntity.ok(new MessageResponse("Logged out successfully"));
    }

    /**
     * POST /api/auth/create-test-doctor
     * Create a test doctor with known password (for testing only)
     */
    @PostMapping("/create-test-doctor")
    public ResponseEntity<?> createTestDoctor() {
        try {
            RegisterRequest request = new RegisterRequest();
            request.setEmail("test@doctor.com");
            request.setPassword("test123");
            request.setFirstName("Test");
            request.setLastName("Doctor");
            request.setRole("DOCTOR");

            AuthResponse response = authService.register(request);
            return ResponseEntity.ok(java.util.Map.of(
                    "message", "Test doctor created successfully!",
                    "email", "test@doctor.com",
                    "password", "test123",
                    "userId", response.getUserId()));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("already registered")) {
                return ResponseEntity.ok(java.util.Map.of(
                        "message", "Test doctor already exists",
                        "email", "test@doctor.com",
                        "password", "test123"));
            }
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // Helper DTOs
    @Data
    public static class RefreshTokenRequest {
        private String refreshToken;
    }

    @Data
    public static class ErrorResponse {
        private final String error;
    }

    @Data
    public static class MessageResponse {
        private final String message;
    }
}
