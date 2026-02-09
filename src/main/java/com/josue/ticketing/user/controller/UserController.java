package com.josue.ticketing.user.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("/")
    public String test() {
        return "test";
    }
}
