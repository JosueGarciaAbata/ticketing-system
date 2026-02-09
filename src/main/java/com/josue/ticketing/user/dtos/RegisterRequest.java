package com.josue.ticketing.user.dtos;

public record RegisterRequest(String email, String password, String firstName, String lastName) {
}
