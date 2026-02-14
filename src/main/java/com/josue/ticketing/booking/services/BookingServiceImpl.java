package com.josue.ticketing.booking.services;

import com.josue.ticketing.booking.dtos.BookingCreateResponse;
import com.josue.ticketing.booking.entities.Booking;
import com.josue.ticketing.booking.entities.BookingSeat;
import com.josue.ticketing.booking.exps.NoAvailableSeatsException;
import com.josue.ticketing.booking.exps.SeatsAlreadyHeldException;
import com.josue.ticketing.booking.redis.RedisSeatHoldService;
import com.josue.ticketing.booking.repos.BookingRepository;
import com.josue.ticketing.booking.repos.BookingSeatRepository;
import com.josue.ticketing.catalog.seat.entities.Seat;
import com.josue.ticketing.catalog.show.entities.Show;
import com.josue.ticketing.catalog.show.exps.ShowNotFoundException;
import com.josue.ticketing.catalog.show.repos.ShowRepository;
import com.josue.ticketing.config.AuthService;
import com.josue.ticketing.payment.dtos.BookingCreateRequest;
import com.josue.ticketing.user.entities.User;
import com.josue.ticketing.user.repos.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements  BookingService {

    private final BookingRepository bookingRepository;
    private final ShowRepository showRepository;
    private final AuthService authService;
    private final UserRepository userRepository;
    private final BookingSeatRepository bookingSeatRepository;
    private final RedisSeatHoldService redisSeatHoldService;

    @Transactional
    @Override
    public BookingCreateResponse create(BookingCreateRequest bookingCreateRequest) {

        Set<Seat> validSeats = bookingRepository.filterAvailableSeatIds(bookingCreateRequest.seatsId());
        if (validSeats.isEmpty()) {
            throw new NoAvailableSeatsException("No hay asientos disponibles para reservar.");
        }

        Integer showId = bookingCreateRequest.showId();
        Show show = showRepository.findById(showId).orElseThrow(() -> new ShowNotFoundException("Funcion no encontrada con id= " + showId));

        Integer userId = authService.getUserId();
        User user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con id= " + userId));

        // Bloquear con redis
        List<Integer> validSeatsId = validSeats.stream().map(Seat::getId).toList();

        UUID bookingPublicId = UUID.randomUUID();
        boolean seatsSuccessfullyHeld = redisSeatHoldService.holdSeats(showId, validSeatsId, bookingPublicId.toString(), 500);
        if (!seatsSuccessfullyHeld) {
            throw new SeatsAlreadyHeldException("Lo sentimos, algunos asientos no pueden ser reservados por el momento.");
        }

        Booking booking = new Booking();
        booking.setPublicId(bookingPublicId);
        booking.setShow(show);
        booking.setUser(user);
        booking.setExpiresAt(ZonedDateTime.now().plusMinutes(5));
        bookingRepository.save(booking);

        List<BookingSeat> bookingSeats = new ArrayList<>();
        for (Seat seat : validSeats) {
            BookingSeat bookingSeat = new BookingSeat();
            bookingSeat.setBooking(booking);
            bookingSeat.setSeat(seat);
            bookingSeats.add(bookingSeat);
        }

        bookingSeatRepository.saveAll(bookingSeats);

        return new BookingCreateResponse(
                booking.getId(),
                showId,
                booking.getStatus()
        );
    }

    @Override
    public void confirm() {

    }

    @Override
    public void cancel() {

    }
}
