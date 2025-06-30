// src/main/java/com/filesharer/securefilesharer/controller/AuthController.java
package com.filesharer.securefilesharer.controller;

import com.filesharer.securefilesharer.dto.AuthRequest;
import com.filesharer.securefilesharer.dto.AuthResponse;
import com.filesharer.securefilesharer.entity.User;
import com.filesharer.securefilesharer.service.UserService;
import com.filesharer.securefilesharer.service.JwtService; // IMPORTANT: Changed import path from .util to .service
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtService jwtUtil; // Keep variable name as jwtUtil for consistency if desired
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registerUser(@Valid @RequestBody AuthRequest request) {
        try {
            // Register user and return success message
            userService.registerUser(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                                 .body(new AuthResponse(null, request.getUsername(), "Registration successful!"));
        } catch (IllegalArgumentException e) {
            // Handle cases where username or email already exists
            return ResponseEntity.badRequest()
                                 .body(new AuthResponse(null, null, e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginUser(@Valid @RequestBody AuthRequest request) {
        // Authenticate user credentials
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        // Get UserDetails from the authenticated principal
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        // Generate JWT token
        String token = jwtUtil.generateToken(userDetails);

        // Return token, username, and a success message
        return ResponseEntity.ok(new AuthResponse(token, userDetails.getUsername(), "Login successful!"));
    }
}
