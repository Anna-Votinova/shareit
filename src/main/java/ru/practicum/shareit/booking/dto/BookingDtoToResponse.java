package ru.practicum.shareit.booking.dto;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.shareit.booking.BookingStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingDtoToResponse implements Serializable {
    private Long id;
    @DateTimeFormat(pattern = "YYYY-MM-DD HH:mm:ss")
    private LocalDateTime start;
    @DateTimeFormat(pattern = "YYYY-MM-DD HH:mm:ss")
    private LocalDateTime end;
    private BookingStatus status;
    private UserDtoBookingToResponse booker;
    private ItemDtoBookingToResponse item;

}
