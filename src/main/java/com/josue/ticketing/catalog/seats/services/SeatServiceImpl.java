package com.josue.ticketing.catalog.seats.services;

import com.josue.ticketing.catalog.seats.dtos.SeatResponse;
import com.josue.ticketing.catalog.seats.entities.Seat;
import com.josue.ticketing.catalog.seats.enums.SeatCategory;
import com.josue.ticketing.catalog.seats.exceps.SeatNotFoundException;
import com.josue.ticketing.catalog.seats.repos.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SeatServiceImpl implements  SeatService {

    private final SeatRepository seatRepository;

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
                        seat.getStatus()
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

        return new SeatResponse(
                seat.getId(),
                seat.getShow().getId(),
                seat.getSeatNumber(),
                seat.getCategory(),
                seat.getStatus()
        );
    }
}
