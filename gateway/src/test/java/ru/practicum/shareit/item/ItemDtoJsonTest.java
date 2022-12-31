package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoLastNextBooking;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
public class ItemDtoJsonTest {

    @Autowired
    private JacksonTester<ItemDto> json;

    private ItemDto itemDto;

    @Test
    void testItemDto_whenFillWithCorrectArguments_thenPositive() throws Exception {

        itemDto = ItemDto.builder()
                .id(1L)
                .name("Щетка для кота")
                .description("Щетка для всех пород котов")
                .available(true)
                .build();

        JsonContent<ItemDto> result = json.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathValue("$.name").isEqualTo("Щетка для кота");
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("Щетка для всех пород котов");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isTrue();

    }

    @Test
    void testItemDto_whenFillWithCorrectArguments_thenReturnFullFilledDto() throws Exception {

        ItemDtoLastNextBooking last = ItemDtoLastNextBooking.builder().id(5L).bookerId(5L).build();
        ItemDtoLastNextBooking next = ItemDtoLastNextBooking.builder().id(6L).bookerId(6L).build();
        Set<CommentDto> comments = new HashSet<>(Set.of(CommentDto.builder()
                .id(1L).text("Отл").authorName("Илья")
                .created(LocalDateTime.parse("2022-12-18T20:00:57")).build()));

        itemDto = ItemDto.builder()
                .id(1L)
                .name("Щетка для кота")
                .description("Щетка для всех пород котов")
                .available(true)
                .lastBooking(last)
                .nextBooking(next)
                .requestId(5L)
                .comments(comments)
                .build();

        JsonContent<ItemDto> result = json.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathValue("$.name").isEqualTo("Щетка для кота");
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("Щетка для всех пород котов");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isTrue();
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.bookerId").isEqualTo(5);
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(5);
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.bookerId").isEqualTo(6);
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.id").isEqualTo(6);
        assertThat(result).extractingJsonPathArrayValue("$.comments").isNotEmpty();
        assertThat(result).extractingJsonPathArrayValue("$.comments").hasSize(1);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(5);
        assertThat(result).extractingJsonPathNumberValue("$.comments[0].id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.comments[0].text").isEqualTo("Отл");
        assertThat(result).extractingJsonPathStringValue("$.comments[0].authorName").isEqualTo("Илья");
        assertThat(result).extractingJsonPathStringValue("$.comments[0].created")
                .isEqualTo("2022-12-18T20:00:57");

    }

    @Test
    void testItemDto_whenFillWithEmptyName_thenFail() {

         itemDto = ItemDto.builder()
                .id(1L)
                .name("")
                .description("Щетка для всех пород котов")
                .available(true)
                .build();

        assertEquals(1, catchViolations(itemDto).size(), "Обнаружено 1 нарушение логики");

    }

    @Test
    void testItemDto_whenFillWithBlankName_thenFail() {

        itemDto = ItemDto.builder()
                .id(1L)
                .name(" ")
                .description("Щетка для всех пород котов")
                .available(true)
                .build();

        assertEquals(1, catchViolations(itemDto).size(), "Обнаружено 1 нарушение логики");

    }

    @Test
    void testItemDto_whenFillWithNullName_thenFail() {

        itemDto = ItemDto.builder()
                .id(1L)
                .name(null)
                .description("Щетка для всех пород котов")
                .available(true)
                .build();

        assertEquals(1, catchViolations(itemDto).size(), "Обнаружено 1 нарушение логики");

    }

    @Test
    void testItemDto_whenFillWithEmptyDescription_thenFail() {

        itemDto = ItemDto.builder()
                .id(1L)
                .name("Щетка для кота")
                .description("")
                .available(true)
                .build();

        assertEquals(1, catchViolations(itemDto).size(), "Обнаружено 1 нарушение логики");

    }

    @Test
    void testItemDto_whenFillWithBlankDescription_thenFail() {

        itemDto = ItemDto.builder()
                .id(1L)
                .name("Щетка для кота")
                .description(" ")
                .available(true)
                .build();

        assertEquals(1, catchViolations(itemDto).size(), "Обнаружено 1 нарушение логики");

    }

    @Test
    void testItemDto_whenFillWithNullDescription_thenFail() {

        itemDto = ItemDto.builder()
                .id(1L)
                .name("Щетка для кота")
                .description(null)
                .available(true)
                .build();

        assertEquals(1, catchViolations(itemDto).size(), "Обнаружено 1 нарушение логики");

    }

    private Set<ConstraintViolation<ItemDto>> catchViolations(ItemDto itemDto) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        return validator.validate(itemDto);
    }


}
