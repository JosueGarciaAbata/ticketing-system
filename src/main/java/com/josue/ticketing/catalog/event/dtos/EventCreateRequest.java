package com.josue.ticketing.catalog.event.dtos;

public record EventCreateRequest(String title, String description, String category, Integer durationMinutes) {
}
