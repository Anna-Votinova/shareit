package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemDtoBookingToResponse {
    private final Long id;
    private final String name;
}
