package com.josue.ticketing.booking.services;

import com.josue.ticketing.booking.dtos.BookingCreateResponse;
import com.josue.ticketing.booking.entities.Booking;
import com.josue.ticketing.booking.entities.BookingSeat;
import com.josue.ticketing.booking.enums.BookingStatus;
import com.josue.ticketing.booking.exps.BookingNotFoundException;
import com.josue.ticketing.booking.exps.NoAvailableSeatsException;
import com.josue.ticketing.booking.exps.SeatsAlreadyHeldException;
import com.josue.ticketing.booking.pk.BookingSeatId;
import com.josue.ticketing.booking.redis.RedisSeatHoldService;
import com.josue.ticketing.booking.repos.BookingRepository;
import com.josue.ticketing.booking.repos.BookingSeatRepository;
import com.josue.ticketing.catalog.seat.entities.Seat;
import com.josue.ticketing.catalog.seat.enums.SeatStatus;
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
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

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
        boolean seatsSuccessfullyHeld = redisSeatHoldService.holdSeats(showId, validSeatsId, bookingPublicId.toString(), 60 * 5);
        if (!seatsSuccessfullyHeld) {
            throw new SeatsAlreadyHeldException("Lo sentimos, algunos asientos no pueden ser reservados por el momento.");
        }

        // Registrar una sincronizacion por si algo falla.
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCompletion(int status) {
                        if (status == STATUS_ROLLED_BACK) {
                            redisSeatHoldService.releaseSeats(showId, validSeatsId);
                        }
                    }
                }
        );

        Booking booking = new Booking();
        booking.setPublicId(bookingPublicId);
        booking.setShow(show);
        booking.setUser(user);
        booking.setExpiresAt(ZonedDateTime.now().plusMinutes(5));
        bookingRepository.save(booking);

        List<BookingSeat> bookingSeats = new ArrayList<>();
        for (Seat seat : validSeats) {
            BookingSeatId bookingSeatId = new BookingSeatId();
            BookingSeat bookingSeat = new BookingSeat();

            bookingSeatId.setSeatId(seat.getId());
            bookingSeat.setBooking(booking);
            bookingSeat.setId(bookingSeatId);
            bookingSeat.setBooking(booking);
            bookingSeat.setSeat(seat);

            bookingSeats.add(bookingSeat);
        }

        bookingSeatRepository.saveAll(bookingSeats);

        return new BookingCreateResponse(
                booking.getPublicId(),
                showId,
                booking.getStatus()
        );
    }

    @Transactional
    @Override
    public void confirm(UUID publicId) {
        Booking booking = bookingRepository.findByPublicId(publicId).orElseThrow(() -> new BookingNotFoundException("Reserva no encontrada con id=" + publicId.toString()));
        if (booking.getExpiresAt().isBefore(ZonedDateTime.now())) {
            throw new IllegalStateException("Reserva expiarad con id= " + publicId.toString());
        }

        if (booking.getStatus() != BookingStatus.ACTIVE) {
            throw new IllegalStateException("Solo reservas activas pueden confirmarse, id= " + publicId.toString());
        }
        booking.setStatus(BookingStatus.CONFIRMED);

        List<BookingSeat> bookingSeats = bookingSeatRepository.findByBookingId(booking.getId());
        List<Integer> seatsId = new ArrayList<>();
        for (BookingSeat bookingSeat : bookingSeats) {
            Seat seat = bookingSeat.getSeat();
            seat.setStatus(SeatStatus.SOLD);
            seatsId.add(seat.getId());
        }

        bookingRepository.save(booking);
        bookingSeatRepository.saveAll(bookingSeats);

        Integer showId =  booking.getShow().getId();
        redisSeatHoldService.releaseSeats(showId, seatsId);
    }

    @Transactional
    @Override
    public void cancel(UUID publicId, String reason) {
        Booking booking = bookingRepository.findByPublicId(publicId).orElseThrow(() -> new BookingNotFoundException("Reserva no encontrada con id=" + publicId.toString()));
        if (booking.getStatus() != BookingStatus.ACTIVE) {
            throw new IllegalStateException("Solo reservas activas pueden confirmarse, id= " + publicId.toString());
        }
        booking.setStatus(BookingStatus.CANCELED);
        booking.setCancelReason(reason);

        List<BookingSeat> bookingSeats = bookingSeatRepository.findByBookingId(booking.getId());
        List<Integer> seatsId = new ArrayList<>();
        for (BookingSeat bookingSeat : bookingSeats) {
            Seat seat = bookingSeat.getSeat();
            seatsId.add(seat.getId());
        }

        bookingRepository.save(booking);
        bookingSeatRepository.deleteAll(bookingSeats);

        Integer showId =  booking.getShow().getId();
        redisSeatHoldService.releaseSeats(showId, seatsId);
    }

    @Override
    public void expire(UUID publicId) {
        Booking booking = bookingRepository.findByPublicId(publicId).orElse(null);
        if (booking == null) {
            return;
        }

        if (booking.getStatus() != BookingStatus.ACTIVE) {
            return;
        }

        booking.setStatus(BookingStatus.CANCELED);
        booking.setCancelReason("timeout");

        List<BookingSeat> bookingSeats =
                bookingSeatRepository.findByBookingId(booking.getId());

        List<Integer> seatIds = bookingSeats.stream()
                .map(bs -> bs.getSeat().getId())
                .toList();

        bookingRepository.save(booking);
        bookingSeatRepository.deleteAll(bookingSeats);
        redisSeatHoldService.releaseSeats(
                booking.getShow().getId(),
                seatIds
        );
    }
}
