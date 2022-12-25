package ru.practicum.shareit.booking;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class BookingMapperTest {

    private final BookingMapper bookingMapper = new BookingMapper();

    private final User user2 = new User(2L, "Olga", "olga@gmail.com");

    private final Item item1 = new Item(1L, "Вещь", "Хорошая вещь", true);

    private final BookingDtoFromRequest requestDto = new BookingDtoFromRequest(
            1L,
            LocalDateTime.parse("2022-11-12T10:09:01"),
            LocalDateTime.parse("2022-12-13T10:09:01"));


    private final Booking booking = new Booking(1L, Timestamp.valueOf("2022-11-12 10:09:01"),
            Timestamp.valueOf("2022-12-13 10:09:01"), item1, user2, BookingStatus.APPROVED);


    @DisplayName("Test for fromDto method")
    @Test
    void givenBookingDto_whenFromDto_thenBookingObject() {

        final Booking fromRequest = bookingMapper.fromDto(requestDto);

        assertThat(fromRequest, notNullValue());
        assertThat(fromRequest.getStart(), equalTo(Timestamp.valueOf(requestDto.getStart())));
        assertThat(fromRequest.getEnd(), equalTo(Timestamp.valueOf(requestDto.getEnd())));

    }

    @DisplayName("Test for toDto method")
    @Test
    void givenBooking_whenToDto_thenBookingDto() {

        final BookingDtoToResponse toResponse = bookingMapper.toDto(booking);

        assertThat(toResponse, notNullValue());
        assertThat(toResponse.getId(), equalTo(1L));
        assertThat(toResponse.getStart(), equalTo(booking.getStart().toLocalDateTime()));
        assertThat(toResponse.getEnd(), equalTo(booking.getEnd().toLocalDateTime()));
        assertThat(toResponse.getItem().getId(), equalTo(1L));
        assertThat(toResponse.getItem().getName(), equalTo("Вещь"));
        assertThat(toResponse.getBooker().getId(), equalTo(2L));
        assertThat(toResponse.getStatus(), equalTo(BookingStatus.APPROVED));

    }

}
