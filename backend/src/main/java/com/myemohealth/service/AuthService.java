package com.myemohealth.service;

import com.myemohealth.dto.auth.AuthResponse;
import com.myemohealth.dto.auth.LoginRequest;
import com.myemohealth.dto.auth.RegisterRequest;
import com.myemohealth.entity.PatientProfile;
import com.myemohealth.entity.Role;
import com.myemohealth.entity.User;
import com.myemohealth.repository.RoleRepository;
import com.myemohealth.repository.UserRepository;
import com.myemohealth.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Authentication service for login, registration, and token management
 */
@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

        private final UserRepository userRepository;
        private final RoleRepository roleRepository;
        private final JwtUtil jwtUtil;
        private final PasswordEncoder passwordEncoder;

        /**
         * Authenticate user and generate tokens
         */
        public AuthResponse login(LoginRequest request) {
                // Find user by email
                User user = userRepository.findByEmail(request.getEmail())
                                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

                // Check if user is enabled
                if (!user.getEnabled()) {
                        throw new RuntimeException("Account is disabled");
                }

                // Verify password (plain text comparison)
                if (!request.getPassword().equals(user.getPasswordHash())) {
                        throw new RuntimeException("Invalid email or password");
                }

                // Update last login
                user.setLastLogin(LocalDateTime.now());
                userRepository.save(user);

                // Generate tokens
                String accessToken = jwtUtil.generateAccessToken(
                                user.getId(),
                                user.getEmail(),
                                user.getRole().getName());

                String refreshToken = jwtUtil.generateRefreshToken(
                                user.getId(),
                                user.getEmail());

                // Build response
                return AuthResponse.builder()
                                .accessToken(accessToken)
                                .refreshToken(refreshToken)
                                .tokenType("Bearer")
                                .expiresIn(3600L) // 1 hour in seconds
                                .userId(user.getId())
                                .email(user.getEmail())
                                .firstName(user.getFirstName())
                                .lastName(user.getLastName())
                                .role(user.getRole().getName())
                                .build();
        }

        /**
         * Register a new user (patient, doctor, or admin)
         */
        public AuthResponse register(RegisterRequest request) {
                // Check if email already exists
                if (userRepository.existsByEmail(request.getEmail())) {
                        throw new RuntimeException("Email already registered");
                }

                // Determine role (default to PATIENT if not specified)
                String roleName = request.getRole() != null ? request.getRole().toUpperCase() : "PATIENT";

                // Validate role
                if (!roleName.equals("PATIENT") && !roleName.equals("DOCTOR") && !roleName.equals("ADMIN")) {
                        throw new RuntimeException("Invalid role. Must be PATIENT, DOCTOR, or ADMIN");
                }

                // Get role from database
                Role role = roleRepository.findByName(roleName)
                                .orElseThrow(() -> new RuntimeException(roleName + " role not found in database"));

                // Create user
                User user = User.builder()
                                .email(request.getEmail())
                                .passwordHash(request.getPassword()) // Store plain text password
                                .firstName(request.getFirstName())
                                .lastName(request.getLastName())
                                .role(role)
                                .enabled(true)
                                .emailVerified(false)
                                .build();

                user = userRepository.save(user);

                // Create patient profile only for PATIENT role
                if ("PATIENT".equals(roleName)) {
                        PatientProfile profile = PatientProfile.builder()
                                        .userId(user.getId())
                                        .user(user)
                                        .dateNaissance(request.getDateNaissance())
                                        .sexe(request.getSexe())
                                        .currentPhase(1)
                                        .consentVoiceRecording(
                                                        request.getConsentVoiceRecording() != null
                                                                        ? request.getConsentVoiceRecording()
                                                                        : false)
                                        .consentDataSharing(request.getConsentDataSharing() != null
                                                        ? request.getConsentDataSharing()
                                                        : false)
                                        .build();

                        user.setPatientProfile(profile);
                        userRepository.save(user);
                }

                // Generate tokens
                String accessToken = jwtUtil.generateAccessToken(
                                user.getId(),
                                user.getEmail(),
                                user.getRole().getName());

                String refreshToken = jwtUtil.generateRefreshToken(
                                user.getId(),
                                user.getEmail());

                // Build response
                return AuthResponse.builder()
                                .accessToken(accessToken)
                                .refreshToken(refreshToken)
                                .tokenType("Bearer")
                                .expiresIn(3600L)
                                .userId(user.getId())
                                .email(user.getEmail())
                                .firstName(user.getFirstName())
                                .lastName(user.getLastName())
                                .role(user.getRole().getName())
                                .build();
        }

        /**
         * Refresh access token
         */
        public AuthResponse refreshToken(String refreshToken) {
                // Validate refresh token
                if (!jwtUtil.validateToken(refreshToken)) {
                        throw new RuntimeException("Invalid refresh token");
                }

                // Extract user info
                String email = jwtUtil.extractEmail(refreshToken);
                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                // Generate new access token
                String newAccessToken = jwtUtil.generateAccessToken(
                                user.getId(),
                                user.getEmail(),
                                user.getRole().getName());

                // Build response
                return AuthResponse.builder()
                                .accessToken(newAccessToken)
                                .refreshToken(refreshToken) // Keep same refresh token
                                .tokenType("Bearer")
                                .expiresIn(3600L)
                                .userId(user.getId())
                                .email(user.getEmail())
                                .firstName(user.getFirstName())
                                .lastName(user.getLastName())
                                .role(user.getRole().getName())
                                .build();
        }

        /**
         * Validate token and get user
         */
        public User validateTokenAndGetUser(String token) {
                if (!jwtUtil.validateToken(token)) {
                        throw new RuntimeException("Invalid token");
                }

                String email = jwtUtil.extractEmail(token);
                return userRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("User not found"));
        }
}
