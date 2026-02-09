package com.josue.ticketing.config;

import com.josue.ticketing.user.dtos.UserDetailsDto;
import com.josue.ticketing.user.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    // Extract username from token - this is the subject claim
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Generate token with just the user details
    public String generateToken(UserDetailsDto user) {
        return generateToken(new HashMap<>(), user);
    }

    // Generate token with extra claims (roles, permissions, etc.)
    public String generateToken(Map<String, Object> extraClaims, UserDetailsDto userDetailsDto) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetailsDto.email())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey())
                .compact();
    }

    // Validate token - check username matches and token is not expired
    public boolean isTokenValid(String token, UserDetailsDto userDetailsDto) {
        final String username = extractUsername(token);
        return (username.equals(userDetailsDto.email())) && !isTokenExpired(token);
    }

    // Check if token has expired
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Extract expiration date from token
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Parse the token and extract all claims
    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Create the signing key from the secret
    private SecretKey getSigningKey() {
        // For production, use a properly generated key
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }
}
