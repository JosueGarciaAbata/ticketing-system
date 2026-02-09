package com.josue.ticketing.user.controller;

import com.josue.ticketing.user.dtos.RegisterRequest;
import com.josue.ticketing.user.dtos.RegisterResponse;
import com.josue.ticketing.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/reg/client")
    public ResponseEntity<RegisterResponse> registerClient(@RequestBody RegisterRequest request) {
        RegisterResponse obtained = userService.register(request, "ROLE_CLIENT");
        return ResponseEntity.status(HttpStatus.CREATED).body(obtained);
    }

    @PostMapping("/reg/organizer")
    public ResponseEntity<RegisterResponse> registerOrganizer(@RequestBody RegisterRequest request) {
        RegisterResponse obtained = userService.register(request, "ROLE_ORGANIZER");
        return ResponseEntity.status(HttpStatus.CREATED).body(obtained);
    }

}
