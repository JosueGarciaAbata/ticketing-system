package com.josue.ticketing.user.service;

import com.josue.ticketing.user.dtos.RegisterRequest;
import com.josue.ticketing.user.dtos.RegisterResponse;

public interface UserService {

    RegisterResponse register(RegisterRequest registerRequest, String roleName);

}
