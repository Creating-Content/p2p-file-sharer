// src/main/java/com/filesharer/securefilesharer/config/jwt/JwtAuthEntryPoint.java
package com.filesharer.securefilesharer.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        // This is invoked when a user tries to access a secured REST resource without supplying any credentials.
        // Sends a 401 Unauthorized response.
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: Authentication required. " + authException.getMessage());
    }
}
