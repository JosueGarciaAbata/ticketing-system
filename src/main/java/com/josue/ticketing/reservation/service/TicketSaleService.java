package com.josue.ticketing.reservation.service;

import com.josue.ticketing.reservation.dtos.TicketCreateRequest;

public interface TicketSaleService {

    String reservate(TicketCreateRequest req);

}
