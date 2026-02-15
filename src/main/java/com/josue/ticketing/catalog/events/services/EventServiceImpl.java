package com.josue.ticketing.catalog.events.services;

import com.josue.ticketing.catalog.events.dtos.EventCreateRequest;
import com.josue.ticketing.catalog.events.dtos.EventResponse;
import com.josue.ticketing.catalog.events.dtos.EventUpdateRequest;
import com.josue.ticketing.catalog.events.entities.Event;
import com.josue.ticketing.catalog.events.exceptions.EventNotFoundException;
import com.josue.ticketing.catalog.events.repos.EventRepository;
import com.josue.ticketing.catalog.events.exceptions.EventHasDependenciesException;
import com.josue.ticketing.catalog.shows.repos.ShowRepository;
import com.josue.ticketing.config.AuthService;
import com.josue.ticketing.user.entities.User;
import com.josue.ticketing.user.repos.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final AuthService authService;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final ShowRepository showRepository;

    @Override
    public List<EventResponse> findAll() {
        return eventRepository.findAll().stream().map(event ->
                new EventResponse(
                        event.getId(),
                        event.getTitle(),
                        event.getDescription(),
                        event.getCategory(),
                        event.getDurationMinutes(),
                        event.getUser().getFullName())).toList();
    }

    @Override
    public EventResponse create(EventCreateRequest req) {

        // Getting from the authentication.
        Integer userId = authService.getUserId();
        User user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con id= " + userId));

        Event event = new Event();
        event.setTitle(req.title());
        event.setDescription(req.description());
        event.setCategory(req.category());
        event.setDurationMinutes(req.durationMinutes());
        event.setUser(user);

        Event created = eventRepository.save(event);

        return new  EventResponse(created.getId(),
                created.getTitle(),
                created.getDescription(),
                created.getCategory(),
                created.getDurationMinutes(),
                user.getFullName());
    }

    @Override
    public EventResponse update(Integer id, EventUpdateRequest req) {

        Event event = eventRepository.findById(id).orElseThrow(() -> new EventNotFoundException("Evento no encontrado con id=" + id));
        event.setTitle(req.title());
        event.setDescription(req.description());
        event.setCategory(req.category());
        event.setDurationMinutes(req.durationMinutes());

        Event updated = eventRepository.save(event);

        return new EventResponse(
                updated.getId(),
                updated.getTitle(),
                updated.getDescription(),
                updated.getCategory(),
                updated.getDurationMinutes(),
                updated.getUser().getFullName()
        );
    }

    @Override
    public void deleteById(Integer id) {
        if (!eventRepository.existsById(id)) {
            throw new EventNotFoundException("Evento no encontrado con id=" + id);
        }

        if (showRepository.existsByEventId((id))) {
            throw new EventHasDependenciesException("El evento tiene funciones asociadas. No se puede eliminar.");
        }
        eventRepository.deleteById(id);
    }
}
