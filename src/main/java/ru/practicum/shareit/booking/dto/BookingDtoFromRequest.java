package ru.practicum.shareit.booking.dto;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Positive;
import java.io.Serializable;
import java.time.LocalDateTime;

@ToString
@Getter
@Setter
@NoArgsConstructor
public class BookingDtoFromRequest implements Serializable {

    @Positive
    private Long itemId;
    @FutureOrPresent(message = "Дата бронирования должна быть в будущем или в настоящем времени")
    @DateTimeFormat(pattern = "YYYY-MM-DD HH:mm:ss")
    private LocalDateTime start;
    @Future(message = "Дата конца бронирования не может быть в прошлом")
    @DateTimeFormat(pattern = "YYYY-MM-DD HH:mm:ss")
    private LocalDateTime end;
}
