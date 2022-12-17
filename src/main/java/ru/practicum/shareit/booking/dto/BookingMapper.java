package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.Booking;

import java.sql.Timestamp;

public class BookingMapper {

    public static Booking fromDto(BookingDtoFromRequest dto) {
        Booking booking = new Booking();
        booking.setStart(Timestamp.valueOf(dto.getStart()));
        booking.setEnd(Timestamp.valueOf(dto.getEnd()));
        return booking;
    }

    public static BookingDtoToResponse toDto(Booking booking) {
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
