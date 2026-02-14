package com.josue.ticketing.jobs;

import com.josue.ticketing.booking.entities.Booking;
import com.josue.ticketing.booking.entities.BookingSeat;
import com.josue.ticketing.booking.enums.BookingStatus;
import com.josue.ticketing.booking.repos.BookingRepository;
import com.josue.ticketing.booking.repos.BookingSeatRepository;
import com.josue.ticketing.booking.services.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JobsService {

    private final BookingRepository bookingRepository;
    private final BookingService bookingService;

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void expireBookings() {
        List<Booking> batch;
        do {
            batch = bookingRepository.findTop100ByStatusAndExpiresAtBefore((BookingStatus.ACTIVE), ZonedDateTime.now());
            batch.forEach(booking -> bookingService.expire(booking.getPublicId()));
        } while (!batch.isEmpty());
    }

}
