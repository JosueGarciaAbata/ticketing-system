package com.josue.ticketing.catalog.venue.services;

import com.josue.ticketing.catalog.city.entities.City;
import com.josue.ticketing.catalog.city.exceps.CityNotFoundException;
import com.josue.ticketing.catalog.city.repos.CityRepository;
import com.josue.ticketing.catalog.show.entities.Show;
import com.josue.ticketing.catalog.venue.dtos.VenueCreateRequest;
import com.josue.ticketing.catalog.venue.dtos.VenueResponse;
import com.josue.ticketing.catalog.venue.dtos.VenueUpdateRequest;
import com.josue.ticketing.catalog.venue.entities.Venue;
import com.josue.ticketing.catalog.venue.exceps.VenueCapacityBelowShowCapacityException;
import com.josue.ticketing.catalog.venue.exceps.VenueHasDependenciesException;
import com.josue.ticketing.catalog.venue.exceps.VenueNotFoundException;
import com.josue.ticketing.catalog.venue.repos.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.Callable;

@Service
@RequiredArgsConstructor
public class VenueServiceImpl implements VenueService {

    private final VenueRepository venueRepository;
    private final CityRepository cityRepository;

    @Override
    public List<VenueResponse> findAll() {
        return venueRepository.findAll().stream().map(venue -> new VenueResponse(venue.getId(), venue.getName(), venue.getCapacity())).toList();
    }

    @Override
    public VenueResponse create(VenueCreateRequest req) {
        City city = cityRepository.findById(req.cityId()).orElseThrow(() -> new CityNotFoundException("Ciudad no encontrada con id=" + req.cityId()));
        Venue venue = new Venue();
        venue.setCity(city);
        venue.setCapacity(req.capacity());
        venue.setName(req.name());
        Venue saved = venueRepository.save(venue);
        return new  VenueResponse(saved.getId(), saved.getName(), saved.getCapacity());
    }

    @Override
    public VenueResponse update(Integer id, VenueUpdateRequest req) {
        City city = cityRepository.findById(req.cityId()).orElseThrow(() -> new CityNotFoundException("Ciudad no encontrada con id=" + req.cityId()));
        Venue venue = venueRepository.findByIdWithShows(id).orElseThrow(() -> new VenueNotFoundException("No se ha encontrado el lugar con id=" + id));
        if (!venue.getShows().isEmpty()) {
            throw new VenueHasDependenciesException("El lugar tiene shows asociados. No se puede actualizar la ciudad");
        }

        int maxShowCapacity = venue.getShows().stream().mapToInt(Show::getCapacity).max().orElse(0);
        if (req.capacity() < maxShowCapacity) {
            throw new VenueCapacityBelowShowCapacityException("La nueva capacidad es inferior a la de algun show existente.");
        }

        venue.setCity(city);
        venue.setName(req.name());
        venue.setCapacity(req.capacity());
        Venue updated = venueRepository.save(venue);

        return new   VenueResponse(updated.getId(), updated.getName(), updated.getCapacity());
    }

    @Override
    public void delete(Integer id) {
        Venue venue = venueRepository.findByIdWithShows(id).orElseThrow(() -> new VenueNotFoundException("No se ha encontrado el lugar con id=" + id));
        if (!venue.getShows().isEmpty()) {
            throw new VenueHasDependenciesException("El lugar tiene shows asociados. No se puede eliminar.");
        }
        venueRepository.delete(venue);
    }
}
