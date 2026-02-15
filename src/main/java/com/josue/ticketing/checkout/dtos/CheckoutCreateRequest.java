package com.josue.ticketing.checkout.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.Set;

public record CheckoutCreateRequest(
                @NotNull @Positive Integer showId,
                @NotEmpty Set<@NotNull @Positive Integer> seatsId) {
}
