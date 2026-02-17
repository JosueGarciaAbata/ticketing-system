package com.josue.ticketing.user.service;

import com.josue.ticketing.user.dtos.RegisterRequest;
import com.josue.ticketing.user.dtos.RegisterResponse;

/**
 * Servicio para gestión de usuarios.
 */
public interface UserService {

    /**
     * Registra un nuevo usuario con el rol especificado.
     * 
     * @param registerRequest datos de registro
     * @param roleName        nombre del rol a asignar
     * @return respuesta con datos del usuario registrado
     */
    RegisterResponse register(RegisterRequest registerRequest, String roleName);

}
