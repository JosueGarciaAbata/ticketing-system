package com.josue.ticketing.catalog.shows.services;

import com.josue.ticketing.booking.entities.Booking;
import com.josue.ticketing.booking.enums.BookingStatus;
import com.josue.ticketing.booking.repos.BookingRepository;
import com.josue.ticketing.booking.repos.BookingSeatRepository;
import com.josue.ticketing.catalog.events.entities.Event;
import com.josue.ticketing.catalog.events.exceptions.EventNotFoundException;
import com.josue.ticketing.catalog.events.repos.EventRepository;
import com.josue.ticketing.catalog.seats.entities.Seat;
import com.josue.ticketing.catalog.seats.entities.SeatPricing;
import com.josue.ticketing.catalog.seats.enums.SeatCategory;
import com.josue.ticketing.catalog.seats.enums.SeatStatus;
import com.josue.ticketing.catalog.seats.repos.SeatPricingRepository;
import com.josue.ticketing.catalog.seats.repos.SeatRepository;
import com.josue.ticketing.catalog.shows.dtos.ShowWithSeatsCreateRequest;
import com.josue.ticketing.catalog.shows.dtos.ShowResponse;
import com.josue.ticketing.catalog.shows.dtos.ShowUpdateRequest;
import com.josue.ticketing.catalog.shows.entities.Show;
import com.josue.ticketing.catalog.shows.enums.ShowStatus;
import com.josue.ticketing.catalog.shows.exps.InsufficientVenueCapacityException;
import com.josue.ticketing.catalog.shows.exps.ShowHasBookingException;
import com.josue.ticketing.catalog.shows.exps.ShowNotFoundException;
import com.josue.ticketing.catalog.shows.repos.ShowRepository;
import com.josue.ticketing.catalog.venues.entities.Venue;
import com.josue.ticketing.catalog.venues.exceps.VenueNotFoundException;
import com.josue.ticketing.catalog.venues.exceps.VenueScheduleConflictException;
import com.josue.ticketing.catalog.venues.repos.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Implementación del servicio de funciones con gestión de asientos.
 */
@Service
@RequiredArgsConstructor
public class ShowServiceImpl implements ShowService {

    private final ShowRepository showRepository;
    private final EventRepository eventRepository;
    private final VenueRepository venueRepository;
    private final SeatRepository seatRepository;
    private final BookingRepository bookingRepository;
    private final BookingSeatRepository bookingSeatRepository;
    private final SeatPricingRepository seatPricingRepository;

    /**
     * Obtiene todas las funciones registradas.
     * 
     * @return lista de funciones con información completa
     */
    @Override
    public List<ShowResponse> findAll() {
        return showRepository.findAll().stream()
                .map(show -> new ShowResponse(
                        show.getId(),
                        show.getEvent().getId(),
                        show.getVenue().getId(),
                        show.getCapacity(),
                        show.getStartTime(),
                        show.getEndTime(),
                        show.getStatus()))
                .toList();
    }

    /**
     * Crea una función completa con asientos y configuración de precios.
     * 
     * @param showWithSeatsCreateRequest datos de la función
     * @return función creada
     */
    @Transactional(readOnly = false)
    @Override
    public ShowResponse createShowWithSeats(ShowWithSeatsCreateRequest showWithSeatsCreateRequest) {
        Show show = create(showWithSeatsCreateRequest);
        createSeatsForShow(show);
        createSeatsPricingForShow(show, showWithSeatsCreateRequest.seatPrice());

        return new ShowResponse(
                show.getId(),
                show.getEvent().getId(),
                show.getVenue().getId(),
                show.getCapacity(),
                show.getStartTime(),
                show.getEndTime(),
                show.getStatus());
    }

    /**
     * Crea la entidad Show validando capacidad y conflictos de horario.
     * 
     * @param req datos de la función
     * @return función creada y persistida
     * @throws EventNotFoundException             si el evento no existe
     * @throws VenueNotFoundException             si el lugar no existe
     * @throws InsufficientVenueCapacityException si no hay capacidad
     * @throws VenueScheduleConflictException     si hay conflicto de horario
     */
    private Show create(ShowWithSeatsCreateRequest req) {

        Integer eventId = req.eventId();
        Integer venueId = req.venueId();
        Integer capacity = req.capacity();

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event no encontrado con id= " + eventId));
        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new VenueNotFoundException("Venue no encontrado"));

        int usedCapacity = showRepository.sumCapacityByVenueId(venueId);
        int availableCapacity = venue.getCapacity() - usedCapacity;

        if (capacity > availableCapacity) {
            throw new InsufficientVenueCapacityException(
                    "No hay suficiente espacio en el lugar. Disponible =" + availableCapacity);
        }

        if (showRepository.existsOverlapBetween(venue.getId(), req.startTime(), req.endTime())) {
            throw new VenueScheduleConflictException("El lugar ya tiene un show programado en ese horario.");
        }

        Show show = new Show();
        show.setCapacity(capacity);
        show.setStartTime(req.startTime());
        show.setEndTime(req.endTime());
        show.setVenue(venue);
        show.setEvent(event);

        return showRepository.save(show);
    }

    /**
     * Crea los asientos para una función (10% VIP, 90% Normal).
     * 
     * @param show función para la cual crear asientos
     */
    private void createSeatsForShow(Show show) {
        int quantityOfSeats = show.getCapacity();
        int vipsSeats = calculateVipSeats(quantityOfSeats);

        List<Seat> seats = IntStream.rangeClosed(1, quantityOfSeats)
                .mapToObj(i -> {
                    Seat seat = new Seat();
                    seat.setShow(show);
                    seat.setSeatNumber("A" + i);

                    if (i <= vipsSeats) {
                        seat.setCategory(SeatCategory.VIP);
                    } else {
                        seat.setCategory(SeatCategory.NORMAL);
                    }
                    return seat;
                })
                .toList();

        seatRepository.saveAll(seats);
    }

    /**
     * Crea la configuración de precios para asientos (VIP 10% más caro).
     * 
     * @param show  función para configurar precios
     * @param price precio base para asientos normales
     */
    private void createSeatsPricingForShow(Show show, BigDecimal price) {

        SeatPricing seatPricing = new SeatPricing();
        seatPricing.setShow(show);
        seatPricing.setPrice(price);
        seatPricing.setCategory(SeatCategory.NORMAL);

        SeatPricing secondSeatPricing = new SeatPricing();
        secondSeatPricing.setShow(show);

        BigDecimal vipSeatPrice = price.multiply(new BigDecimal("1.10")).setScale(2, RoundingMode.UP);
        secondSeatPricing.setPrice(vipSeatPrice);
        secondSeatPricing.setCategory(SeatCategory.VIP);

        seatPricingRepository.saveAll(List.of(seatPricing, secondSeatPricing));

    }

    /**
     * Calcula la cantidad de asientos VIP (10% del total).
     * 
     * @param quantityOfSeats cantidad total de asientos
     * @return cantidad de asientos VIP
     */
    private int calculateVipSeats(int quantityOfSeats) {
        return (int) Math.ceil(quantityOfSeats * 0.10);
    }

    /**
     * Actualiza los horarios de una función validando conflictos.
     * 
     * @param id  identificador de la función
     * @param req nuevos horarios
     * @return función actualizada
     * @throws ShowNotFoundException          si la función no existe
     * @throws VenueScheduleConflictException si hay conflicto de horario
     */
    @Override
    @Transactional
    public ShowResponse update(Integer id, ShowUpdateRequest req) {

        Show show = showRepository.findById(id)
                .orElseThrow(() -> new ShowNotFoundException("Funcion no encontrada con id= " + id));
        if (showRepository.existsOverlapBetweenExcludingShowId(show.getVenue().getId(), req.startTime(), req.endTime(),
                show.getId())) {
            throw new VenueScheduleConflictException("El lugar ya tiene un show programado en ese horario.");
        }

        show.setStartTime(req.startTime());
        show.setEndTime(req.endTime());
        showRepository.save(show);

        return new ShowResponse(
                show.getId(),
                show.getEvent().getId(),
                show.getVenue().getId(),
                show.getCapacity(),
                show.getStartTime(),
                show.getEndTime(),
                show.getStatus());
    }

    /**
     * Cancela una función liberando reservas activas y marcando como cancelado.
     * 
     * @param showId identificador de la función
     * @throws ShowNotFoundException   si la función no existe
     * @throws ShowHasBookingException si hay reservas confirmadas
     */
    @Transactional(readOnly = false)
    @Override
    public void cancelBookingAndReleaseSeats(Integer showId) {
        // Existe el show
        Show show = showRepository.findById(showId)
                .orElseThrow(() -> new ShowNotFoundException("Funcion no encontrada con id= " + showId));

        // Con funciones reservadas...
        if (bookingRepository.existsConfirmedBookingByShowId(showId)) {
            throw new ShowHasBookingException("La funcion ya tiene reservas confirmadas. No puede ser cancelada.");
        }

        // Con funcinoes activas
        List<Booking> activeBookings = bookingRepository.findAllActiveBookingsByShowId(showId);

        // Se liberan asientos
        activeBookings.forEach(b -> bookingSeatRepository.deleteByBookingId(b.getId()));

        // Y se cancelan reservas
        activeBookings.forEach(b -> {
            b.setStatus(BookingStatus.CANCELED);
            b.setCancelReason("El show fue cancelado.");
        });

        bookingRepository.saveAll(activeBookings);
        show.setStatus(ShowStatus.CANCELED);
        showRepository.save(show);
    }
}
