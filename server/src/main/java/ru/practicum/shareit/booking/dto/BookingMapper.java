package ru.practicum.shareit.booking.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;

import java.sql.Timestamp;

@Component
@RequiredArgsConstructor
public class BookingMapper {

    public Booking fromDto(BookingDtoFromRequest dto) {
        Booking booking = new Booking();
        booking.setStart(Timestamp.valueOf(dto.getStart()));
        booking.setEnd(Timestamp.valueOf(dto.getEnd()));
        return booking;
    }

    public BookingDtoToResponse toDto(Booking booking) {
        BookingDtoToResponse dto = new BookingDtoToResponse();
        dto.setId(booking.getId());
        dto.setStart(booking.getStart().toLocalDateTime());
        dto.setEnd(booking.getEnd().toLocalDateTime());
        dto.setStatus(booking.getStatus());
        dto.setBooker(UserDtoBookingToResponse.builder()
                        .id(booking.getBooker().getId())
                .build());
        dto.setItem(ItemDtoBookingToResponse.builder()
                        .id(booking.getItem().getId())
                        .name(booking.getItem().getName())
                .build());
        return dto;
    }
}
