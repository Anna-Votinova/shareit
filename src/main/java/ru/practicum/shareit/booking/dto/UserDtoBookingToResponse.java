package ru.practicum.shareit.booking.dto;

import lombok.*;

import java.io.Serializable;

@ToString
@Getter
@Setter
@Builder
public class UserDtoBookingToResponse implements Serializable {
    private final Long id;
}
