package com.josue.ticketing.catalog.event.dtos;

public record EventResponse(Integer id, String title, String description, String category, Integer durationMinutes, String organizerFullName) {
}
