package ru.practicum.shareit.item;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Set;

@ToString
@Getter
@Setter
@Builder
public class ItemDto implements Serializable {


    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private ItemDtoLastNextBooking lastBooking;
    private ItemDtoLastNextBooking nextBooking;
    private Long requestId;
    private Set<CommentDto> comments;

}
