package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class ItemDtoLastNextBooking implements Serializable {
    private final Long id;
    private final Long bookerId;
}
