package com.josue.ticketing.payment.repos;

import com.josue.ticketing.payment.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    boolean existsByProviderAndProviderReference(String provider, String providerReference);
}
