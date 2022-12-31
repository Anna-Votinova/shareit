package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.shareit.Constants.DATE_TIME;

@ToString
@Getter
@Setter
@Builder
public class ItemRequestDto {

    private Long id;
    private String description;
    @DateTimeFormat(pattern = DATE_TIME)
    private LocalDateTime created;
    private List<ItemRequestInfo> items;
}
