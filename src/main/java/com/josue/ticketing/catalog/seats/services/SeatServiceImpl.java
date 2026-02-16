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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeatServiceImpl implements  SeatService {

    private final SeatRepository seatRepository;
    private final SeatPricingRepository seatPricingRepository;

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
                        getPriceByCategory(showId, seat.getCategory())
                ))
                .toList();
    }

    @Transactional
    @Override
    public SeatResponse changeCategory(Integer id, SeatCategory category) {

        Seat seat = seatRepository.findById(id).orElseThrow(() -> new SeatNotFoundException("Asiento no encontrado con id= " + id));

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
                getPriceByCategory(showId, category)
        );
    }

    private BigDecimal getPriceByCategory(Integer showId, SeatCategory category) {
        Map<SeatCategory, BigDecimal> priceByCategory =
                seatPricingRepository.findAllByShowId(showId)
                        .stream()
                        .collect(Collectors.toMap(
                                SeatPricing::getCategory,
                                SeatPricing::getPrice
                        ));

        return priceByCategory.get(category);
    }
}
