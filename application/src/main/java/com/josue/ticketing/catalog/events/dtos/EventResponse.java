package com.josue.ticketing.catalog.events.dtos;

public record EventResponse(Integer id, String title, String description, String category, Integer durationMinutes, String organizerFullName) {
}
