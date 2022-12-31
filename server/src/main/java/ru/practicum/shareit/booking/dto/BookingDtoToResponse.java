package ru.practicum.shareit.booking.dto;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.shareit.booking.BookingStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

import static ru.practicum.shareit.Constants.DATE_TIME;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingDtoToResponse implements Serializable {
    private Long id;
    @DateTimeFormat(pattern = DATE_TIME)
    private LocalDateTime start;
    @DateTimeFormat(pattern = DATE_TIME)
    private LocalDateTime end;
    private BookingStatus status;
    private UserDtoBookingToResponse booker;
    private ItemDtoBookingToResponse item;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BookingDtoToResponse)) return false;
        return id != null && id.equals(((BookingDtoToResponse) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
