package com.josue.ticketing.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

/**
 * Filtro de validación JWT que verifica tokens en cada solicitud.
 */
public class JwtValidationFilter extends OncePerRequestFilter {
    private JwtService jwtService;
    private UserDetailsService userDetailsService;

    /**
     * Constructor del filtro de validación JWT.
     * 
     * @param jwtService         servicio para operaciones con JWT
     * @param userDetailsService servicio para cargar detalles de usuario
     */
    public JwtValidationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Filtra las solicitudes validando el token JWT del header Authorization.
     * 
     * @param request     solicitud HTTP entrante
     * @param response    respuesta HTTP
     * @param filterChain cadena de filtros
     * @throws ServletException si ocurre error del servlet
     * @throws IOException      si ocurre error de E/S
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.replace("Bearer ", "");
        try {
            Claims claims = jwtService.extractAllClaims(token);
            UserDetails userdetails = userDetailsService.loadUserByUsername(claims.getSubject());
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userdetails,
                    null, userdetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } catch (JwtException e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");

            Map<String, String> body = Map.of(
                    "error", "invalid_token",
                    "message", "El token JWT es inválido o expiró");

            response.getWriter().write(new ObjectMapper().writeValueAsString(body));
        }
    }

    /**
     * Determina si el filtro debe omitirse para ciertas rutas públicas.
     * 
     * @param request solicitud HTTP
     * @return true si la ruta es pública y no requiere validación JWT
     * @throws ServletException si ocurre error del servlet
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        return path.startsWith("/api/v1/login") || path.startsWith("/api/v1/users/register");
    }
}
