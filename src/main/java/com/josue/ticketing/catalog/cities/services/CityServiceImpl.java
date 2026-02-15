package com.josue.ticketing.catalog.cities.services;

import com.josue.ticketing.catalog.cities.dtos.CityCreateRequest;
import com.josue.ticketing.catalog.cities.dtos.CityResponse;
import com.josue.ticketing.catalog.cities.dtos.CityUpdateRequest;
import com.josue.ticketing.catalog.cities.entities.City;
import com.josue.ticketing.catalog.cities.exceptions.CityAlreadyExistsException;
import com.josue.ticketing.catalog.cities.exceptions.CityHasDependenciesException;
import com.josue.ticketing.catalog.cities.exceptions.CityNotFoundException;
import com.josue.ticketing.catalog.cities.repos.CityRepository;
import com.josue.ticketing.catalog.venues.repos.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CityServiceImpl implements CityService {

    private final CityRepository cityRepository;
    private final VenueRepository venueRepository;

    @Transactional(readOnly = true)
    @Override
    public List<CityResponse> findAll() {
        return cityRepository.findAll().stream().map(city -> new CityResponse(
                city.getId(),
                city.getName(),
                city.getCountry(),
                city.getTimezone()
        )).toList();
    }

    @Transactional(readOnly = false)
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

    @Transactional(readOnly = false)
    @Override
    public CityResponse update(Integer id, CityUpdateRequest req) {
        City city = cityRepository.findById(id).orElseThrow(() -> new CityNotFoundException("Ciudad no encontrada con id= " + id));
        String name = req.name().trim();
        String country = req.country().trim();
        String timezone = req.timezone().trim();

        // unique(name, country)
        boolean alreadyExists = cityRepository.existsByNameAndCountryAndIdNot(name, country, city.getId());
        if (alreadyExists) {
            throw new CityAlreadyExistsException("El nombre y pais de la ciudad ya se encuentran registrados.");
        }

        if (!name.equals(city.getName())) {
            city.setName(name);
        }

        if (!country.equals(city.getCountry())) {
            city.setCountry(country);
        }

        if (!timezone.equals(city.getTimezone())) {
            city.setTimezone(timezone);
        }

        cityRepository.save(city);

        return new CityResponse(
                city.getId(),
                city.getName(),
                city.getCountry(),
                city.getTimezone()
        );
    }

    @Transactional(readOnly = false)
    @Override
    public void deleteById(Integer id) {
        if (!cityRepository.existsById(id)) {
            throw new CityNotFoundException("Ciudad no encontrada con id= " + id);
        }
        if (venueRepository.existsByCityId(id)) {
            throw new CityHasDependenciesException("La ciudad tiene lugares asociados. Borralos primero antes de proseguir.");
        }
        cityRepository.deleteById(id);
    }
}
