package com.josue.ticketing.jobs;

import com.josue.ticketing.booking.entities.Booking;
import com.josue.ticketing.booking.enums.BookingStatus;
import com.josue.ticketing.booking.repos.BookingRepository;
import com.josue.ticketing.booking.services.BookingService;
import com.josue.ticketing.catalog.shows.repos.ShowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Servicio de tareas programadas para mantenimiento del sistema.
 */
@Service
@RequiredArgsConstructor
public class JobsService {

    private final BookingRepository bookingRepository;
    private final BookingService bookingService;
    private final ShowRepository showRepository;

    /**
     * Expira reservas activas que han superado su tiempo límite.
     * Se ejecuta cada 60 segundos.
     */
    @Scheduled(fixedRate = 60000)
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void expireBookings() {
        List<Booking> batch;
        do {
            batch = bookingRepository.findTop100ByStatusAndExpiresAtBefore((BookingStatus.ACTIVE), ZonedDateTime.now());
            batch.forEach(booking -> bookingService.expire(booking.getPublicId()));
        } while (!batch.isEmpty());
    }

    /**
     * Marca como finalizados los shows que ya pasaron su hora de fin.
     * Se ejecuta cada hora (3600000 ms).
     */
    @Scheduled(fixedRate = 3600000)
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void markShowsAsFinished() {
        showRepository.markAsFinished();
    }
}
