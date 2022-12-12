package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemDtoLastNextBooking {
    private final Long id;
    private final Long bookerId;
}
