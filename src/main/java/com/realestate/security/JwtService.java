package com.realestate.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.util.*;

@Service
public class JwtService {
    private final String secret;
    private final long expirationMs;

    public JwtService(@Value("${app.jwt.secret}") String secret,
                      @Value("${app.jwt.expiration-ms}") long expirationMs) {
        this.secret = secret;
        this.expirationMs = expirationMs;
    }

    public String generateToken(AppUserDetails user) {
        Date now = new Date();
        return Jwts.builder().subject(user.getUsername()).claim("role", user.user().getRole().name())
            .issuedAt(now).expiration(new Date(now.getTime() + expirationMs)).signWith(key()).compact();
    }

    public String extractUsername(String token) {
        return claims(token).getSubject();
    }

    public boolean isValid(String token, AppUserDetails user) {
        try {
            return extractUsername(token).equalsIgnoreCase(user.getUsername()) && claims(token).getExpiration().after(new Date());
        } catch (JwtException | IllegalArgumentException exception) {
            return false;
        }
    }

    private Claims claims(String token) {
        return Jwts.parser().verifyWith(key()).build().parseSignedClaims(token).getPayload();
    }

    private SecretKey key() {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("JWT_SECRET must be configured with a Base64-encoded 256-bit key.");
        }
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }
}

