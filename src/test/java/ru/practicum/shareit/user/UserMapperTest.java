package ru.practicum.shareit.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class UserMapperTest {

    private final UserMapper userMapper = new UserMapper();

    private final UserDto userDto = new UserDto(
            1L,
            "Anna",
            "anna13@36on.ru"
    );

    private final User user = new User(
            1L,
            "Anna",
            "anna13@36on.ru"
    );

    @DisplayName("Test for fromDto method")
    @Test
    void givenUserDto_whenFromDto_thenUserObject() {

        final User fromRequest = userMapper.fromDto(userDto);

        assertThat(fromRequest, notNullValue());
        assertThat(fromRequest.getId(), equalTo(1L));
        assertThat(fromRequest.getName(), equalTo("Anna"));
        assertThat(fromRequest.getEmail(), equalTo("anna13@36on.ru"));

    }

    @DisplayName("Test for fromItem method")
    @Test
    void givenItem_whenFromItem_thenItemDto() {

        final UserDto toResponse = userMapper.fromUser(user);

        assertThat(toResponse, notNullValue());
        assertThat(toResponse.getId(), equalTo(1L));
        assertThat(toResponse.getName(), equalTo("Anna"));
        assertThat(toResponse.getEmail(), equalTo("anna13@36on.ru"));

    }
}
