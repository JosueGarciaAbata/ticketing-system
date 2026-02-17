package com.josue.ticketing.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Configuración de seguridad de la aplicación con JWT.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    /**
     * Configura la cadena de filtros de seguridad HTTP.
     * 
     * @param http                    configurador de seguridad HTTP
     * @param jwtAuthenticationFilter filtro de autenticación JWT
     * @param jwtValidationFilter     filtro de validación JWT
     * @return cadena de filtros configurada
     * @throws Exception si ocurre error en la configuración
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
            JwtAuthenticationFilter jwtAuthenticationFilter,
            JwtValidationFilter jwtValidationFilter) throws Exception {
        http
                // Disable CSRF - not needed for stateless JWT auth
                .csrf(csrf -> csrf.disable())
                .cors(cors -> corsConfigurationSource())

                // Stateless session - no server-side session storage
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Configure authorization rules
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints - no auth required
                        .requestMatchers("/api/v1/login").permitAll()
                        .requestMatchers("/api/v1/users/reg/**").permitAll()
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/webhook").permitAll()
                        // Everything else requires authentication
                        .anyRequest().authenticated())

                // Add JWT filter before the standard authentication filter
                .addFilterBefore(jwtValidationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilter(jwtAuthenticationFilter);

        return http.build();
    }

    /**
     * Proporciona el administrador de autenticación.
     * 
     * @param config configuración de autenticación
     * @return administrador de autenticación
     * @throws Exception si ocurre error al obtener el administrador
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Proporciona el codificador de contraseñas BCrypt.
     * 
     * @return codificador de contraseñas
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configura las políticas CORS para la aplicación.
     * 
     * @return fuente de configuración CORS
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "DELETE", "PUT"));
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    /**
     * Crea el filtro de autenticación JWT.
     * 
     * @param authenticationManager administrador de autenticación
     * @param jwtService            servicio JWT
     * @return filtro de autenticación configurado
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(AuthenticationManager authenticationManager,
            JwtService jwtService) {
        return new JwtAuthenticationFilter(authenticationManager, jwtService);
    }

    /**
     * Crea el filtro de validación JWT.
     * 
     * @param jwtService         servicio JWT
     * @param userDetailsService servicio de detalles de usuario
     * @return filtro de validación configurado
     */
    @Bean
    public JwtValidationFilter jwtValidationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        return new JwtValidationFilter(jwtService, userDetailsService);
    }
}
