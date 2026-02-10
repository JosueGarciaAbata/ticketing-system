package com.josue.ticketing.config;

import com.josue.ticketing.user.entities.UserDetailsImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private Logger logger = LoggerFactory.getLogger(AuthService.class);

    public UserDetailsImpl getUserDetails() {

        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();

        logger.info("Iniciando autenticacion");
        if (auth == null || auth instanceof AnonymousAuthenticationToken) {
            throw new AccessDeniedException("Usuario no autenticado");
        }
        Object principal = auth.getPrincipal();
        logger.info("Si hay un objeto principal");
        logger.info("Principal class   : {}", principal.getClass().getName());
        logger.info("Principal toString: {}", principal);

        if (!(principal instanceof UserDetailsImpl userDetails)) {
            throw new AccessDeniedException("Principal inválido");
        }
        logger.info("Devolviendo el princiapl");

        return userDetails;
    }

    public Integer getUserId() {
        return getUserDetails().getId();
    }

}
