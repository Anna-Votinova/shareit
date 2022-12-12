package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDtoBookingToResponse {
    private final Long id;
}
