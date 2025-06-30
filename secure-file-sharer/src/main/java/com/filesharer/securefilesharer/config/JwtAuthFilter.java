// src/main/java/com/filesharer/securefilesharer/config/jwt/JwtAuthFilter.java
package com.filesharer.securefilesharer.config;

import com.filesharer.securefilesharer.service.JwtService;
import com.filesharer.securefilesharer.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService; // Use UserService to load UserDetails

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // 1. Check for JWT presence
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        username = jwtService.extractUsername(jwt); // Extract username from JWT

        // 2. Validate JWT and set SecurityContext if user not already authenticated
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userService.loadUserByUsername(username); // Load user details

            if (jwtService.isTokenValid(jwt, userDetails)) {
                // If token is valid, create an authentication object
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null, // Credentials are null as token is already validated
                        userDetails.getAuthorities() // Get authorities (roles) from UserDetails
                );
                // Set authentication details from the request
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                // Set the authentication in the SecurityContextHolder
                SecurityContextHolder.getContext().setAuthentication(authToken);
                System.out.println("Authenticated user: " + username + " with roles: " + userDetails.getAuthorities());
            } else {
                System.out.println("JWT token is invalid for user: " + username);
            }
        } else {
            if (username == null) {
                System.out.println("Username could not be extracted from JWT or JWT is null.");
            }
            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                System.out.println("User " + SecurityContextHolder.getContext().getAuthentication().getName() + " already authenticated.");
            }
        }
        filterChain.doFilter(request, response); // Continue the filter chain
    }
}
