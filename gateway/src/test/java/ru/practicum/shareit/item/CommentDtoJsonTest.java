package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.CommentDto;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
public class CommentDtoJsonTest {

    @Autowired
    private JacksonTester<CommentDto> json;

    private CommentDto commentDto;


    @BeforeEach
    public void setUp() {

        commentDto = CommentDto.builder()
                .id(1L)
                .text("Отл")
                .authorName("Илья")
                .created(LocalDateTime.parse("2022-12-18T20:00:57"))
                .build();
    }

    @Test
    void testItemDto_whenFillWithCorrectArguments_thenPositive() throws Exception {

        JsonContent<CommentDto> result = json.write(commentDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("Отл");
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("Илья");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2022-12-18T20:00:57");

    }

    @Test
    void testCommentDto_whenFillWithEmptyText_thenFail() {
        commentDto.setText("");

        assertEquals(1, catchViolations(commentDto).size(), "Обнаружено 1 нарушение логики");

    }

    @Test
    void testCommentDto_whenFillWithBlankText_thenFail() {
        commentDto.setText(" ");

        assertEquals(1, catchViolations(commentDto).size(), "Обнаружено 1 нарушение логики");

    }

    @Test
    void testCommentDto_whenFillWithNullText_thenFail() {
        commentDto.setText(null);

        assertEquals(1, catchViolations(commentDto).size(), "Обнаружено 1 нарушение логики");

    }

    private Set<ConstraintViolation<CommentDto>> catchViolations(CommentDto commentDto) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        return validator.validate(commentDto);
    }
}
