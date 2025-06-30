// src/main/java/com/filesharer/securefilesharer/service/UserService.java
package com.filesharer.securefilesharer.service;

import com.filesharer.securefilesharer.dto.AuthRequest; // Import AuthRequest
import com.filesharer.securefilesharer.entity.Role; // Import Role enum
import com.filesharer.securefilesharer.entity.User;
import com.filesharer.securefilesharer.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder; // Import PasswordEncoder
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections; // For Collections.singleton

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // Inject PasswordEncoder

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    @Transactional(readOnly = true)
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    @Transactional
    public void registerUser(AuthRequest request) {
        // Check if username already exists
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username '" + request.getUsername() + "' is already taken.");
        }
        // Check if email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) { // Assuming findByEmail exists in UserRepository
            throw new IllegalArgumentException("Email '" + request.getEmail() + "' is already registered.");
        }

        User newUser = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword())) // Hash the password
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .roles(Collections.singleton(Role.USER)) // Assign default ROLE_USER
                .build();
        userRepository.save(newUser);
    }
}
