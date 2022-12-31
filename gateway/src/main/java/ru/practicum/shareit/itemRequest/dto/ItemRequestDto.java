package ru.practicum.shareit.itemRequest.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.shareit.Constants.DATE_TIME;

@ToString
@Getter
@Setter
@Builder
public class ItemRequestDto {

    private Long id;
    @NotBlank
    private String description;
    @DateTimeFormat(pattern = DATE_TIME)
    private LocalDateTime created;
    private List<ItemRequestInfo> items;
}
