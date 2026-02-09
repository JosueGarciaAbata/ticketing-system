package com.josue.ticketing.user.dtos;

import lombok.Builder;

@Builder
public record RegisterResponse(String email, String fullName) {
}
