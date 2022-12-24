package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserIntegrationTest {


    private final UserRepository userRepository;
    private final UserController userController;


    @DisplayName("Integration test for findAll method")
    @Test
    public void givenUsers_whenGetAllUsers_thenListOfUsers() {
        User user1 = new User(null, "Anna", "anna13@36on.ru");
        User user2 = new User(null, "Olga", "olga@gmail.com");

        userRepository.save(user1);
        userRepository.save(user2);

        List<UserDto> savedList = userController.getAllUsers();

        assertThat(savedList.size(), equalTo(2));
        assertThat(savedList.get(0).getId(), notNullValue());
        assertThat(savedList.get(0).getName(), equalTo(user1.getName()));
        assertThat(savedList.get(0).getEmail(), equalTo(user1.getEmail()));
        assertThat(savedList.get(1).getId(), notNullValue());
        assertThat(savedList.get(1).getName(), equalTo(user2.getName()));
        assertThat(savedList.get(1).getEmail(), equalTo(user2.getEmail()));

    }

    @DisplayName("Integration test for findAll method (negative scenario)")
    @Test
    public void givenNoUsers_whenGetAllUsers_thenThrowException() {

        assertThrows(IllegalArgumentException.class, userController::getAllUsers);
    }

}
