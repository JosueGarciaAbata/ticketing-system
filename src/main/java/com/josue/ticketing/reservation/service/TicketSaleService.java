package com.josue.ticketing.reservation.service;

import com.josue.ticketing.payment.dtos.PaymentStatusResponse;
import com.josue.ticketing.reservation.dtos.TicketCreateRequest;

public interface TicketSaleService {

    String reservate(TicketCreateRequest req) throws Exception;
    PaymentStatusResponse getPaymentStatusBySessionId(String sessionId);
    void expireStripeSession(String sessionId);


}
