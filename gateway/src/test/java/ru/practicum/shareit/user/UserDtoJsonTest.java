package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
public class UserDtoJsonTest {

    @Autowired
    private JacksonTester<UserDto> json;

    @Test
    void testUserDto_whenFillWithCorrectArguments_thenPositive() throws Exception {
        UserDto userDto2 = new UserDto(
                2L,
                "Anna",
                "anna13@36on.ru"
        );

        JsonContent<UserDto> result = json.write(userDto2);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Anna");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("anna13@36on.ru");

    }

    @Test
    void testUserDto_whenFillWithEmptyName_thenFail() {

        UserDto userDto2 = new UserDto(
                2L,
                "",
                "anna13@36on.ru"
        );

        assertEquals(1, catchViolations(userDto2).size(), "Обнаружено 1 нарушение логики");

    }

    @Test
    void testUserDto_whenFillWithBlankName_thenFail() {

        UserDto userDto2 = new UserDto(
                2L,
                " ",
                "anna13@36on.ru"
        );

        assertEquals(1, catchViolations(userDto2).size(), "Обнаружено 1 нарушение логики");

    }

    @Test
    void testUserDto_whenFillWithNullName_thenFail() {

        UserDto userDto2 = new UserDto(
                2L,
                null,
                "anna13@36on.ru"
        );

        assertEquals(1, catchViolations(userDto2).size(), "Обнаружено 1 нарушение логики");

    }

    @Test
    void testUserDto_whenFillWithEmptyEmailAndNullName_thenFail() {

        UserDto userDto2 = new UserDto(
                2L,
                null,
                ""
        );

        assertEquals(2, catchViolations(userDto2).size(), "Обнаружено 2 нарушения логики");

    }

    @Test
    void testUserDto_whenFillWithEmptyEmail_thenFail() {

        UserDto userDto2 = new UserDto(
                2L,
                "Anna",
                ""
        );

        assertEquals(1, catchViolations(userDto2).size(), "Обнаружено 1 нарушение логики");

    }

    @Test
    void testUserDto_whenFillWithBlankEmail_thenFail() {

        UserDto userDto2 = new UserDto(
                2L,
                "Anna",
                " "
        );

        assertEquals(2, catchViolations(userDto2).size(), "Обнаружено 2 нарушения логики");

    }

    @Test
    void testUserDto_whenFillWithNullEmail_thenFail() {

        UserDto userDto2 = new UserDto(
                2L,
                "Anna",
                null
        );

        assertEquals(1, catchViolations(userDto2).size(), "Обнаружено 1 нарушение логики");

    }

    @Test
    void testUserDto_whenFillWithWrongEmail_thenFail() {

        UserDto userDto2 = new UserDto(
                2L,
                "Anna",
                "anna1336on.ru"
        );

        assertEquals(1, catchViolations(userDto2).size(), "Обнаружено 1 нарушение логики");

    }


    private Set<ConstraintViolation<UserDto>> catchViolations(UserDto userDto) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        return validator.validate(userDto);
    }
}
