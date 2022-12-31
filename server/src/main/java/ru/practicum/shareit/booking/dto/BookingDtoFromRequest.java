package ru.practicum.shareit.booking.dto;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

import static ru.practicum.shareit.Constants.DATE_TIME;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingDtoFromRequest implements Serializable {


    private Long itemId;
    @DateTimeFormat(pattern = DATE_TIME)
    private LocalDateTime start;
    @DateTimeFormat(pattern = DATE_TIME)
    private LocalDateTime end;
}