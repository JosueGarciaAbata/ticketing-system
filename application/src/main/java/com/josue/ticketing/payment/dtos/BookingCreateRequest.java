package com.josue.ticketing.payment.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.Set;

public record BookingCreateRequest(
    @NotNull @Positive Integer showId,
    @NotEmpty Set<@NotNull @Positive Integer> seatsId) {

}
