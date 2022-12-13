package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class UserDtoBookingToResponse implements Serializable {
    private final Long id;
}
