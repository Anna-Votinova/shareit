package ru.practicum.shareit.item.dto;

import lombok.*;

import java.io.Serializable;

@ToString
@Getter
@Setter
@Builder
public class ItemDtoLastNextBooking implements Serializable {
    private final Long id;
    private final Long bookerId;
}
