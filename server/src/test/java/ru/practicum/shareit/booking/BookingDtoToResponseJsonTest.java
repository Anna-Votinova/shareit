package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDtoToResponse;
import ru.practicum.shareit.booking.dto.ItemDtoBookingToResponse;
import ru.practicum.shareit.booking.dto.UserDtoBookingToResponse;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class BookingDtoToResponseJsonTest {

    @Autowired
    private JacksonTester<BookingDtoToResponse> json;

    @Test
    void testBookingDtoFromRequestDto_whenFillWithCorrectArguments_thenPositive() throws Exception {
        UserDtoBookingToResponse booker = UserDtoBookingToResponse.builder().id(1L).build();
        ItemDtoBookingToResponse item = ItemDtoBookingToResponse.builder().id(1L).name("Щетка для кота").build();

        BookingDtoToResponse response = new BookingDtoToResponse(
                1L,
                LocalDateTime.parse("2023-11-12T10:09:00"),
                LocalDateTime.parse("2023-12-13T10:09:00"),
                BookingStatus.APPROVED,
                booker,
                item
        );

        JsonContent<BookingDtoToResponse> result = json.write(response);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2023-11-12T10:09:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2023-12-13T10:09:00");
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualToIgnoringCase("APPROVED");
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("Щетка для кота");

    }

}
