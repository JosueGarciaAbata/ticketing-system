package com.josue.ticketing.booking.services;

import com.josue.ticketing.booking.dtos.BookingCreateResponse;
import com.josue.ticketing.booking.entities.Booking;
import com.josue.ticketing.booking.entities.BookingSeat;
import com.josue.ticketing.booking.enums.BookingStatus;
import com.josue.ticketing.booking.exceptions.BookingNotFoundException;
import com.josue.ticketing.booking.exceptions.NoAvailableSeatsException;
import com.josue.ticketing.booking.exceptions.SeatsAlreadyHeldException;
import com.josue.ticketing.booking.embbeded.BookingSeatId;
import com.josue.ticketing.booking.redis.RedisSeatHoldService;
import com.josue.ticketing.booking.repos.BookingRepository;
import com.josue.ticketing.booking.repos.BookingSeatRepository;
import com.josue.ticketing.catalog.seats.entities.Seat;
import com.josue.ticketing.catalog.seats.entities.SeatPricing;
import com.josue.ticketing.catalog.seats.enums.SeatCategory;
import com.josue.ticketing.catalog.seats.enums.SeatStatus;
import com.josue.ticketing.catalog.seats.repos.SeatPricingRepository;
import com.josue.ticketing.catalog.shows.entities.Show;
import com.josue.ticketing.catalog.shows.enums.ShowStatus;
import com.josue.ticketing.catalog.shows.exps.ShowNotFoundException;
import com.josue.ticketing.catalog.shows.repos.ShowRepository;
import com.josue.ticketing.config.AuthService;
import com.josue.ticketing.payment.dtos.BookingCreateRequest;
import com.josue.ticketing.payment.entities.Payment;
import com.josue.ticketing.payment.enums.PaymentStatus;
import com.josue.ticketing.payment.repos.PaymentRepository;
import com.josue.ticketing.user.entities.User;
import com.josue.ticketing.user.repos.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ShowRepository showRepository;
    private final AuthService authService;
    private final UserRepository userRepository;
    private final BookingSeatRepository bookingSeatRepository;
    private final RedisSeatHoldService redisSeatHoldService;

    private final int ttlSeatHold = 480; // 8 minutos
    private final int bookingExpiresAt = 300; // 5 minutos
    private final PaymentRepository paymentRepository;
    private final SeatPricingRepository seatPricingRepository;

    @Override
    public BookingCreateResponse create(BookingCreateRequest bookingCreateRequest) {
        Integer showId = bookingCreateRequest.showId();
        Set<Integer> requestedSeatsId = bookingCreateRequest.seatsId();
        List<Integer> seatsIdList = new ArrayList<>(requestedSeatsId);

        // 1. FAIL-FAST: Intentar bloquear en Redis PRIMERO (operación atómica, ~2-3ms)
        // Si falla, retornamos inmediatamente SIN tocar la BD
        UUID bookingPublicId = UUID.randomUUID();
        boolean seatsSuccessfullyHeld = redisSeatHoldService.holdSeats(
                showId, seatsIdList, bookingPublicId.toString(), ttlSeatHold);

        if (!seatsSuccessfullyHeld) {
            throw new SeatsAlreadyHeldException(
                    "Lo sentimos, algunos asientos no pueden ser reservados por el momento.");
        }

        // 2. Si llegamos aquí, tenemos el lock en Redis. Ahora validamos y persistimos.
        try {
            return persistBooking(bookingCreateRequest, showId, seatsIdList, bookingPublicId);
        } catch (Exception e) {
            // Liberar Redis si algo falla en la validación/persistencia
            redisSeatHoldService.releaseSeats(showId, seatsIdList);
            throw e;
        }
    }

    @Transactional
    protected BookingCreateResponse persistBooking(
            BookingCreateRequest bookingCreateRequest,
            Integer showId,
            List<Integer> seatsIdList,
            UUID bookingPublicId) {

        // Validar asientos disponibles en BD
        Set<Seat> validSeats = bookingRepository.filterAvailableSeatIds(bookingCreateRequest.seatsId());
        if (validSeats.isEmpty()) {
            throw new NoAvailableSeatsException("No hay asientos disponibles para reservar.");
        }

        // Validar Show
        Show show = showRepository.findById(showId)
                .orElseThrow(() -> new ShowNotFoundException("Funcion no encontrada con id= " + showId));

        if (show.getStatus().equals(ShowStatus.CANCELED) || show.getStatus().equals(ShowStatus.FINISHED)) {
            throw new IllegalStateException("El show ha sido cancelado/terminado, no se puede reservar. Id=" + showId);
        }

        // Obtener usuario
        Integer userId = authService.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con id= " + userId));

        // Persistir Booking
        Booking booking = new Booking();
        booking.setPublicId(bookingPublicId);
        booking.setShow(show);
        booking.setUser(user);
        booking.setExpiresAt(ZonedDateTime.now().plusSeconds(bookingExpiresAt));
        bookingRepository.save(booking);

        // Persistir BookingSeats
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
                booking.getStatus());
    }

    @Override
    public BookingCreateResponse createDbOnly(BookingCreateRequest bookingCreateRequest) {
        Set<Seat> validSeats = bookingRepository.filterAvailableSeatIds(bookingCreateRequest.seatsId());
        if (validSeats.isEmpty()) {
            throw new NoAvailableSeatsException("No hay asientos disponibles para reservar.");
        }

        Integer showId = bookingCreateRequest.showId();
        Show show = showRepository.findById(showId)
                .orElseThrow(() -> new ShowNotFoundException("Funcion no encontrada con id= " + showId));

        if (show.getStatus().equals(ShowStatus.CANCELED) || show.getStatus().equals(ShowStatus.FINISHED)) {
            throw new IllegalStateException("El show ha sido cancelado/terminado, no se puede reservar. Id=" + showId);
        }

        Integer userId = authService.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con id= " + userId));

        // Validacion SOLO contra BD (sin Redis): asientos no vendidos + no actualmente
        // retenidos (ACTIVE no expirado)
        List<Integer> validSeatsId = validSeats.stream().map(Seat::getId).toList();
        List<Integer> currentlyHeldSeatIds = bookingSeatRepository.findCurrentlyHeldSeatIds(
                showId,
                validSeatsId,
                ZonedDateTime.now());
        if (!currentlyHeldSeatIds.isEmpty()) {
            throw new SeatsAlreadyHeldException(
                    "Lo sentimos, algunos asientos no pueden ser reservados por el momento.");
        }

        UUID bookingPublicId = UUID.randomUUID();

        Booking booking = new Booking();
        booking.setPublicId(bookingPublicId);
        booking.setShow(show);
        booking.setUser(user);
        booking.setExpiresAt(ZonedDateTime.now().plusSeconds(bookingExpiresAt));
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
                booking.getStatus());
    }

    @Override
    @Transactional
    @SuppressWarnings("null")
    public void confirm(UUID publicId) {
        Booking booking = bookingRepository.findByPublicId(publicId)
                .orElseThrow(() -> new BookingNotFoundException("Reserva no encontrada con id=" + publicId.toString()));
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

        Integer showId = booking.getShow().getId();
        redisSeatHoldService.releaseSeats(showId, seatsId);
    }

    @Override
    @Transactional
    @SuppressWarnings("null")
    public void cancel(UUID publicId, String reason) {
        Booking booking = bookingRepository.findByPublicId(publicId)
                .orElseThrow(() -> new BookingNotFoundException("Reserva no encontrada con id=" + publicId.toString()));
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

        Integer showId = booking.getShow().getId();
        redisSeatHoldService.releaseSeats(showId, seatsId);
    }

    @Override
    @Transactional
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

        int capacity = booking.getShow().getCapacity();
        int vipSeats = calculateVipSeats(capacity);
        int normalSeats = capacity - vipSeats;

        Map<SeatCategory, BigDecimal> prices = seatPricingRepository.findAllByShowId(booking.getShow().getId())
                .stream()
                .collect(Collectors.toMap(
                        SeatPricing::getCategory,
                        SeatPricing::getPrice));

        BigDecimal finalAmount = prices.getOrDefault(SeatCategory.NORMAL, BigDecimal.ZERO)
                .multiply(BigDecimal.valueOf(normalSeats))
                .add(
                        prices.getOrDefault(SeatCategory.VIP, BigDecimal.ZERO)
                                .multiply(BigDecimal.valueOf(vipSeats)));

        Payment failedPayment = new Payment();
        failedPayment.setPublicId(UUID.randomUUID());
        failedPayment.setBooking(booking);
        failedPayment.setProvider("SYSTEM");
        failedPayment.setProviderReference("bk_" + publicId.toString());
        failedPayment.setAmount(finalAmount);
        failedPayment.setStatus(PaymentStatus.FAILED);

        List<BookingSeat> bookingSeats = bookingSeatRepository.findByBookingId(booking.getId());

        List<Integer> seatIds = bookingSeats.stream()
                .map(bs -> bs.getSeat().getId())
                .toList();

        bookingRepository.save(booking);
        bookingSeatRepository.deleteAll(bookingSeats);
        paymentRepository.save(failedPayment);
        redisSeatHoldService.releaseSeats(
                booking.getShow().getId(),
                seatIds);
    }

    private int calculateVipSeats(int quantityOfSeats) {
        return (int) Math.ceil(quantityOfSeats * 0.10);
    }
}
