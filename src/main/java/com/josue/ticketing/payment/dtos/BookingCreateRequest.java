package com.josue.ticketing.payment.dtos;

import java.util.Set;

public record BookingCreateRequest(Integer showId, Set<Integer> seatsId) {

}
