package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class ItemDtoBookingToResponse implements Serializable {
    private final Long id;
    private final String name;
}
