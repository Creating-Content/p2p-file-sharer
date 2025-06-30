// src/main/java/com/filesharer/securefilesharer/service/JwtService.java
package com.filesharer.securefilesharer.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm; // Correct import for 0.11.x signing algorithm
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key; // Use java.security.Key
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;

    // Returns a signing key derived from the secretKey
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Generates a JWT token for the given UserDetails without extra claims
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    // Generates a JWT token with provided extra claims
    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ) {
        // Correct API for JJWT 0.11.x: use setClaims and SignatureAlgorithm
        return Jwts.builder()
                .setClaims(extraClaims) // Use setClaims for 0.11.x
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512) // Use SignatureAlgorithm for 0.11.x
                .compact();
    }

    // Extracts the subject (username) from the JWT token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Extracts the expiration date from the JWT token
    private Date extractExpiration(String token) { // Private helper method
        return extractClaim(token, Claims::getExpiration);
    }

    // Generic method to extract a specific claim using a ClaimsResolver function
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Parses the JWT token and returns all claims
    private Claims extractAllClaims(String token) {
        try {
            // Correct API for JJWT 0.11.x to parse:
            return Jwts.parserBuilder() // Use parserBuilder() for immutable parser
                    .setSigningKey(getSigningKey())
                    .build() // Build the parser instance
                    .parseClaimsJws(token) // Parse the JWS (Signed JWT)
                    .getBody(); // Get the claims body
        } catch (JwtException e) {
            // Log specific JWT exceptions for debugging
            logger.error("Error parsing JWT: {}", e.getMessage());
            throw e; // Re-throw to be caught by JwtAuthFilter for proper HTTP response
        }
    }

    // Validates the JWT token against UserDetails
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            // Check if username matches and token is not expired
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (JwtException e) {
            logger.error("JWT validation failed for token: {} - {}", token, e.getMessage());
            return false;
        }
    }

    // Checks if the token's expiration date is before the current date
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
}
