package ru.practicum.shareit.item;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@ToString
@Getter
@Setter
@Builder
public class ItemDtoLastNextBooking implements Serializable {
    private final Long id;
    private final Long bookerId;
}
