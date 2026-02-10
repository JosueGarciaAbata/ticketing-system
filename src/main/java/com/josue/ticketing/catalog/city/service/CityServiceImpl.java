package com.josue.ticketing.catalog.city.service;

import com.josue.ticketing.catalog.city.dtos.CityCreateRequest;
import com.josue.ticketing.catalog.city.dtos.CityResponse;
import com.josue.ticketing.catalog.city.dtos.CityUpdateRequest;
import com.josue.ticketing.catalog.city.entities.City;
import com.josue.ticketing.catalog.city.excep.CityAlreadyExistsException;
import com.josue.ticketing.catalog.city.excep.CityHasDependenciesException;
import com.josue.ticketing.catalog.city.excep.CityNotFoundException;
import com.josue.ticketing.catalog.city.repo.CityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CityServiceImpl implements CityService {

    private final CityRepository cityRepository;

    @Override
    public List<CityResponse> findAll() {
        return cityRepository.findAll().stream().map(city -> new CityResponse(
                city.getId(),
                city.getName(),
                city.getCountry(),
                city.getTimezone()
        )).toList();
    }

    @Override
    public CityResponse create(CityCreateRequest req) {
        City city = new City();
        city.setName(req.name());
        city.setCountry(req.country());
        city.setTimezone(req.timezone());

        cityRepository.save(city);

        return new CityResponse(
                city.getId(),
                city.getName(),
                city.getCountry(),
                city.getTimezone()
        );
    }

    @Override
    public CityResponse update(Integer id, CityUpdateRequest req) {
        City city = cityRepository.findById(id).orElseThrow(() -> new CityNotFoundException("Ciudad no encontrada con id= " + id));

        // unique(name, country)
        boolean isUnique = cityRepository.existsByNameAndCountry(req.name(), req.country());
        if (!isUnique) {
            throw new CityAlreadyExistsException("El nombre y pais de la ciudad ya se encuentran registrados.");
        }

        city.setTimezone(req.timezone());
        city.setName(req.name());
        city.setCountry(req.country());

        cityRepository.save(city);

        return new CityResponse(
                city.getId(),
                city.getName(),
                city.getCountry(),
                city.getTimezone()
        );
    }

    @Override
    public void delete(Integer id) {
        City city = cityRepository.findByIdWithVenues(id).orElseThrow(() -> new CityNotFoundException("Ciudad no encontrada con id= " + id));
        if (!city.getVenue().isEmpty()) {
            throw new CityHasDependenciesException("La ciudad tiene lugares asociados. Borralos primero antes de proseguir.");
        }
        cityRepository.delete(city);
    }
}
