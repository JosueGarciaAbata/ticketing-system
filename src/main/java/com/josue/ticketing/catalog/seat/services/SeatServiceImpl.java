package com.josue.ticketing.catalog.seat.services;

import com.josue.ticketing.catalog.seat.dtos.SeatResponse;
import com.josue.ticketing.catalog.seat.entities.Seat;
import com.josue.ticketing.catalog.seat.enums.SeatCategory;
import com.josue.ticketing.catalog.seat.exceps.SeatNotFoundException;
import com.josue.ticketing.catalog.seat.repos.SeatRepository;
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
