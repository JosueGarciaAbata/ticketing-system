package com.josue.ticketing.catalog.event.dtos;

public record EventUpdateRequest(String title, String description, String category, Integer durationMinutes) {
}
