package com.example.netflixai.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JWTUtil {

    // Use Base64-encoded key (minimum 256 bits for HS256)
    private static final String SECRET_KEY = "MZt4eW82aVRkRjY1R3N3YkltN3h2Z0VmbU5YdnJhZ0pUd2N6aE5NVXlEbkN3bA=="; // example key (Base64 encoded)
    private final SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY));

    // Generate JWT token
    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hour expiry
                .signWith(key)
                .compact();
    }

    // Extract username (subject) from token
    public String extractUsername(String token) {
        return getAllClaims(token).getSubject();
    }

    // Validate the token
    public boolean validateToken(String token) {
        try {
            Claims claims = getAllClaims(token);
            Date expiration = claims.getExpiration();
            return expiration.after(new Date());
        } catch (Exception e) {
            System.out.println(" Invalid or expired token: " + e.getMessage());
            return false;
        }
    }

    // Helper: parse token and return all claims
    private Claims getAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
