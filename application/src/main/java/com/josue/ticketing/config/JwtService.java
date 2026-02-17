package com.josue.ticketing.config;

import com.josue.ticketing.user.dtos.UserDetailsDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Servicio para manejo de tokens JWT: generación, validación y extracción de
 * claims.
 */
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    /**
     * Extrae el nombre de usuario (subject) del token JWT.
     * 
     * @param token token JWT
     * @return nombre de usuario contenido en el token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrae un claim específico del token usando un resolver de claims.
     * 
     * @param token          token JWT
     * @param claimsResolver función para extraer el claim deseado
     * @param <T>            tipo del claim a extraer
     * @return valor del claim extraído
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Genera un token JWT solo con los detalles del usuario.
     * 
     * @param user detalles del usuario
     * @return token JWT generado
     */
    public String generateToken(UserDetailsDto user) {
        return generateToken(new HashMap<>(), user);
    }

    /**
     * Genera un token JWT con claims adicionales (roles, permisos, etc.).
     * 
     * @param extraClaims    claims adicionales a incluir en el token
     * @param userDetailsDto detalles del usuario
     * @return token JWT generado
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetailsDto userDetailsDto) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetailsDto.email())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Valida si el token es válido verificando usuario y expiración.
     * 
     * @param token          token JWT a validar
     * @param userDetailsDto detalles del usuario para comparar
     * @return true si el token es válido, false en caso contrario
     */
    public boolean isTokenValid(String token, UserDetailsDto userDetailsDto) {
        final String username = extractUsername(token);
        return (username.equals(userDetailsDto.email())) && !isTokenExpired(token);
    }

    /**
     * Verifica si el token ha expirado.
     * 
     * @param token token JWT
     * @return true si el token ha expirado
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extrae la fecha de expiración del token.
     * 
     * @param token token JWT
     * @return fecha de expiración del token
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Parsea el token y extrae todos los claims.
     * 
     * @param token token JWT
     * @return todos los claims del token
     */
    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Crea la clave de firma a partir del secreto configurado.
     * 
     * @return clave secreta para firmar tokens
     */
    private SecretKey getSigningKey() {
        // For production, use a properly generated key
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }
}
