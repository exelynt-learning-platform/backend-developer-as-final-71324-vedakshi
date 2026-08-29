package com.booking.resource_booking_system.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private final String secretKey =
            "mySecretKeyForResourceBookingSystem123456789";

    private final long expirationTime = 1000 * 60 * 60;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(
                secretKey.getBytes(StandardCharsets.UTF_8)
        );
    }

    public String generateToken(String username, String role) {

    return Jwts.builder()
            .subject(username)
            .claim("role", role)
            .issuedAt(new Date())
            .expiration(
                    new Date(System.currentTimeMillis() + expirationTime)
            )
            .signWith(getSigningKey())
            .compact();
}

    public String extractUsername(String token) {

        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean isTokenValid(String token, String username) {

        String tokenUsername = extractUsername(token);

        return tokenUsername.equals(username);
    }
}