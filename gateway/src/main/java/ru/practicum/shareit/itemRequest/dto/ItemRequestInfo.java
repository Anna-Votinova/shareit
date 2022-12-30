package ru.practicum.shareit.itemRequest.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@ToString
@Getter
@Setter
@Builder
public class ItemRequestInfo implements Serializable {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
}
