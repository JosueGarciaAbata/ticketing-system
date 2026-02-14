package com.josue.ticketing.reservation.dtos;

import java.util.Set;

public record TicketCreateRequest(Integer showId, Set<Integer> seatsId) {
}
