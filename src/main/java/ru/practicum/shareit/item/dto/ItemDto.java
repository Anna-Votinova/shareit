package ru.practicum.shareit.item.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Set;

@ToString
@Getter
@Setter
@Builder
@AllArgsConstructor
public class ItemDto implements Serializable {


    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    private Boolean available;
    private ItemDtoLastNextBooking lastBooking;
    private ItemDtoLastNextBooking nextBooking;
    private Long requestId;
    private Set<CommentDto> comments;

}
