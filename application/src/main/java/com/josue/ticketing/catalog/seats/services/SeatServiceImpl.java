package com.josue.ticketing.catalog.seats.services;

import com.josue.ticketing.catalog.seats.dtos.SeatResponse;
import com.josue.ticketing.catalog.seats.entities.Seat;
import com.josue.ticketing.catalog.seats.entities.SeatPricing;
import com.josue.ticketing.catalog.seats.enums.SeatCategory;
import com.josue.ticketing.catalog.seats.exceps.SeatNotFoundException;
import com.josue.ticketing.catalog.seats.repos.SeatPricingRepository;
import com.josue.ticketing.catalog.seats.repos.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementación del servicio de asientos.
 */
@Service
@RequiredArgsConstructor
public class SeatServiceImpl implements SeatService {

    private final SeatRepository seatRepository;
    private final SeatPricingRepository seatPricingRepository;

    /**
     * Obtiene todos los asientos de un show con sus precios.
     * 
     * @param showId identificador del show
     * @return lista de asientos con información de precio y estado
     */
    @Transactional(readOnly = true)
    @Override
    public List<SeatResponse> findAllByShowId(Integer showId) {

        return seatRepository.findAllByShowId(showId)
                .stream()
                .map(seat -> new SeatResponse(
                        seat.getId(),
                        seat.getShow().getId(),
                        seat.getSeatNumber(),
                        seat.getCategory(),
                        seat.getStatus(),
                        getPriceByCategory(showId, seat.getCategory())))
                .toList();
    }

    /**
     * Cambia la categoría de un asiento.
     * 
     * @param id       identificador del asiento
     * @param category nueva categoría (VIP o NORMAL)
     * @return asiento actualizado con nuevo precio
     * @throws SeatNotFoundException si el asiento no existe
     */
    @Transactional
    @Override
    public SeatResponse changeCategory(Integer id, SeatCategory category) {

        Seat seat = seatRepository.findById(id)
                .orElseThrow(() -> new SeatNotFoundException("Asiento no encontrado con id= " + id));

        if (!seat.getCategory().equals(category)) {
            seat.setCategory(category);
        }
        seatRepository.save(seat);

        Integer showId = seat.getShow().getId();

        return new SeatResponse(
                seat.getId(),
                showId,
                seat.getSeatNumber(),
                seat.getCategory(),
                seat.getStatus(),
                getPriceByCategory(showId, category));
    }

    /**
     * Obtiene el precio de un asiento según su categoría.
     * 
     * @param showId   identificador del show
     * @param category categoría del asiento
     * @return precio del asiento
     * @throws IllegalStateException si no existe precio para la categoría
     */
    private BigDecimal getPriceByCategory(Integer showId, SeatCategory category) {
        Map<SeatCategory, BigDecimal> map = new HashMap<>();
        List<SeatPricing> pricingList = seatPricingRepository.findAllByShowId(showId);

        for (SeatPricing pricing : pricingList) {
            map.put(pricing.getCategory(), pricing.getPrice());
        }

        BigDecimal price = map.get(category);
        if (price == null) {
            throw new IllegalStateException(
                    "No se encontró el precio para la categoría " + category + " en showId=" + showId);
        }
        return price;
    }
}
