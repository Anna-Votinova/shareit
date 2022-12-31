package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
public class BookItemRequestDtoJsonTest {

    @Autowired
    private JacksonTester<BookItemRequestDto> json;

    private BookItemRequestDto dto;

    @BeforeEach
    public void setUp() {
        dto = new BookItemRequestDto(
                1L,
                LocalDateTime.parse("2023-11-12T10:09:00"),
                LocalDateTime.parse("2023-12-13T10:09:00"));

    }

    @Test
    void testBookingDtoFromRequestDto_whenFillWithCorrectArguments_thenPositive() throws Exception {

        JsonContent<BookItemRequestDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2023-11-12T10:09:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2023-12-13T10:09:00");

    }

    @Test
    void testBookingDtoFromRequest_whenPastStart_theFail() {

        dto.setStart(LocalDateTime.parse("2022-11-12T10:09:00"));

        assertEquals(1, catchViolations(dto).size(), "Обнаружено 1 нарушение логики");
    }

    @Test
    void testBookingDtoFromRequest_whenNowStart_theNotFail() {

        dto.setEnd(LocalDateTime.parse("2022-11-12T10:09:00"));

        assertEquals(1, catchViolations(dto).size(), "Обнаружено 1 нарушений логики");
    }

    private Set<ConstraintViolation<BookItemRequestDto>> catchViolations(BookItemRequestDto dto) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        return validator.validate(dto);
    }

}
