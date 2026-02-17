package com.josue.ticketing.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.josue.ticketing.user.dtos.UserDetailsDto;
import com.josue.ticketing.user.dtos.UserLoginRequest;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Filtro de autenticación JWT que procesa las solicitudes de login.
 */
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private JwtService jwtService;

    /**
     * Constructor del filtro de autenticación JWT.
     * 
     * @param manager    administrador de autenticación de Spring Security
     * @param jwtService servicio para generación de tokens JWT
     */
    public JwtAuthenticationFilter(AuthenticationManager manager, JwtService jwtService) {
        super.setAuthenticationManager(manager);
        this.setFilterProcessesUrl("/api/v1/login");
        this.jwtService = jwtService;
    }

    /**
     * Intenta autenticar al usuario extrayendo credenciales del cuerpo de la
     * solicitud.
     * 
     * @param request  solicitud HTTP con credenciales de login
     * @param response respuesta HTTP
     * @return resultado de la autenticación
     * @throws AuthenticationException si la autenticación falla
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        String email = null;
        String password = null;
        try {
            UserLoginRequest userLoginRequest = new ObjectMapper().readValue(request.getInputStream(),
                    UserLoginRequest.class);
            email = userLoginRequest.email();
            password = userLoginRequest.password();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email,
                password);
        return super.getAuthenticationManager().authenticate(authenticationToken);
    }

    /**
     * Procesa una autenticación exitosa generando y devolviendo un token JWT.
     * 
     * @param request    solicitud HTTP
     * @param response   respuesta HTTP donde se escribe el token
     * @param chain      cadena de filtros
     * @param authResult resultado de la autenticación exitosa
     * @throws IOException      si ocurre error de E/S
     * @throws ServletException si ocurre error del servlet
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
            Authentication authResult) throws IOException, ServletException {
        // Spring Security User
        UserDetails user = (UserDetails) authResult.getPrincipal();
        String username = user.getUsername();

        Collection<? extends GrantedAuthority> roles = user.getAuthorities();
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles.stream().map(GrantedAuthority::getAuthority).toList());
        claims.put("email", username);

        UserDetailsDto userDetailsDto = new UserDetailsDto(username);
        String token = jwtService.generateToken(claims, userDetailsDto);

        Map<String, String> body = new HashMap<>();
        body.put("token", token);
        body.put("email", username);
        body.put("message", "Has iniciado sesino con exito " + username);

        response.getWriter().write(new ObjectMapper().writeValueAsString(body));
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
    }

    /**
     * Procesa una autenticación fallida devolviendo un mensaje de error.
     * 
     * @param request  solicitud HTTP
     * @param response respuesta HTTP con mensaje de error
     * @param failed   excepción de autenticación fallida
     * @throws IOException      si ocurre error de E/S
     * @throws ServletException si ocurre error del servlet
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException failed) throws IOException, ServletException {
        Map<String, Object> body = new HashMap<>();
        body.put("message", "Credenciales invalidas.");
        response.getWriter().write(new ObjectMapper().writeValueAsString(body));
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
    }
}
