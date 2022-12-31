package ru.practicum.shareit.request;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.itemRequest.dto.ItemRequestDto;
import ru.practicum.shareit.itemRequest.dto.ItemRequestInfo;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
public class ItemRequestDtoJsonTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    void testItemRequestDto_whenFillWithCorrectArguments_thenReturnItemDtoWithoutItems() throws Exception {

        ItemRequestDto dto = ItemRequestDto.builder()
                .id(1L)
                .description("Хотел бы взять в аренду чесалку для кота")
                .created(LocalDateTime.parse("2022-12-18T20:00:57"))
                .items(null)
                .build();

        JsonContent<ItemRequestDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("Хотел бы взять в аренду чесалку для кота");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2022-12-18T20:00:57");
        assertThat(result).extractingJsonPathValue("$.items").isNull();

    }

    @Test
    void testItemRequestDto_whenFillWithCorrectArguments_thenReturnItemDtoWithItems() throws Exception {

        ItemRequestInfo info = ItemRequestInfo.builder()
                .id(1L)
                .name("Щетка для кота")
                .description("Щетка для всех пород котов")
                .available(true)
                .requestId(1L)
                .build();

        ItemRequestDto dto = ItemRequestDto.builder()
                .id(1L)
                .description("Хотел бы взять в аренду чесалку для кота")
                .created(LocalDateTime.parse("2022-12-18T20:00:57"))
                .items(List.of(info))
                .build();

        JsonContent<ItemRequestDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("Хотел бы взять в аренду чесалку для кота");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2022-12-18T20:00:57");
        assertThat(result).extractingJsonPathValue("$.items").isNotNull();
        assertThat(result).extractingJsonPathValue("$.items[0].name").isEqualTo("Щетка для кота");

    }

    @Test
    void testItemRequestDto_whenFillWithEmptyDescription_thenFail() {

        ItemRequestDto dto = ItemRequestDto.builder()
                .id(1L)
                .description("")
                .created(LocalDateTime.parse("2022-12-18T20:00:57"))
                .items(List.of(ItemRequestInfo.builder().build()))
                .build();


        assertEquals(1, catchViolations(dto).size(), "Обнаружено 1 нарушение логики");

    }

    @Test
    void testItemRequestDto_whenFillWithBlankDescription_thenFail() {

        ItemRequestDto dto = ItemRequestDto.builder()
                .id(1L)
                .description(" ")
                .created(LocalDateTime.parse("2022-12-18T20:00:57"))
                .items(List.of(ItemRequestInfo.builder().build()))
                .build();


        assertEquals(1, catchViolations(dto).size(), "Обнаружено 1 нарушение логики");

    }

    @Test
    void testItemRequestDto_whenFillWithNullDescription_thenFail() {

        ItemRequestDto dto = ItemRequestDto.builder()
                .id(1L)
                .description(null)
                .created(LocalDateTime.parse("2022-12-18T20:00:57"))
                .items(List.of(ItemRequestInfo.builder().build()))
                .build();


        assertEquals(1, catchViolations(dto).size(), "Обнаружено 1 нарушение логики");

    }

    private Set<ConstraintViolation<ItemRequestDto>> catchViolations(ItemRequestDto itemRequestDto) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        return validator.validate(itemRequestDto);
    }

}
