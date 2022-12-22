package ru.practicum.shareit.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper mapper;
    @InjectMocks
    private UserServiceImpl userService;

    private UserDto userDto;

    private User user;


    @BeforeEach
    public void setUp() {

        userDto = new UserDto(
                1L,
                "Anna",
                "anna13@36on.ru"
        );

        user = new User(
                1L,
                "Anna",
                "anna13@36on.ru"
        );

    }

    @AfterEach
    public void verifyInteractions() {
        verifyNoMoreInteractions(userRepository);
    }

    @DisplayName("JUnit test for create method")
    @Test
    public void givenUserDto_whenCreateUser_thenReturnUserObject() {

        given(mapper.fromDto(userDto)).willReturn(user);

        given(userRepository.save(user)).willReturn(user);

        given(mapper.fromUser(user)).willReturn(userDto);

        UserDto savedUser = userService.create(userDto);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser).isEqualTo(userDto);

    }

    @DisplayName("JUnit test for create method which throws exception")
    @Test
    public void givenUserWithExistingEmail_whenSaveUser_thenThrowsException() {

        UserDto userDto2 = new UserDto(
                2L,
                "Anna",
                "anna13@36on.ru"
        );

        User user2 = new User(
                2L,
                "Anna",
                "anna13@36on.ru"
        );


        given(mapper.fromDto(userDto2)).willReturn(user2);

        given(userRepository.save(user2)).willThrow(DataIntegrityViolationException.class);


        assertThrows(RuntimeException.class, () -> userService.create(userDto2));

    }

    @DisplayName("JUnit test for getAll method")
    @Test
    public void givenUserList_whenGetAllUsers_thenReturnUserDtoList() {


        User user2 = new User(
                2L,
                "Anna",
                "anna@36on.ru"
        );

        given(userRepository.findAll()).willReturn(List.of(user, user2));


        List<UserDto> userDtoList = userService.getAll();

        assertThat(userDtoList).isNotNull();
        assertThat(userDtoList.size()).isEqualTo(2);


    }

    @DisplayName("JUnit test for getAll method (negative scenario)")
    @Test
    public void givenEmptyUserList_whenGetAllUsers_thenThrowsException() {

        given(userRepository.findAll()).willReturn(Collections.emptyList());

        assertThrows(IllegalArgumentException.class, () -> userService.getAll());

    }

    @DisplayName("JUnit test for getUserById method")
    @Test
    public void givenUserDtoId_whenGetUserById_thenReturnUserObject() {

        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        given(mapper.fromUser(user)).willReturn(userDto);

        Optional<UserDto> savedUser = userService.getUserById(user.getId());

        assertThat(savedUser).isNotNull();

    }

    @DisplayName("JUnit test for getUserById method (negative scenario)")
    @Test
    public void givenUserDtoInCorrectId_whenGetUserById_thenThrowsException() {

        given(userRepository.findById(-1L)).willThrow(IllegalArgumentException.class);

        assertThrows(IllegalArgumentException.class, () -> userService.getUserById(-1L));

    }

    @DisplayName("JUnit test for update method")
    @Test
    public void givenUserObject_whenUpdateUser_thenReturnUpdatedUser() {


        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        user.setEmail("anna@gmail.com");
        user.setName("Anya");

        given(userRepository.save(user)).willReturn(user);
        given(mapper.fromUser(user)).willReturn(new UserDto(1L, "Anya", "anna@gmail.com"));

        Optional<UserDto> updatedUser = userService.update(user.getId(), new UserDto(1L, "Anya", "anna@gmail.com"));


        assertThat(updatedUser.get().getEmail()).isEqualTo("anna@gmail.com");
        assertThat(updatedUser.get().getName()).isEqualTo("Anya");

    }

    @DisplayName("JUnit test for deleteUserById method")
    @Test
    public void givenUserId_whenDeleteUser_thenNothing() {

        Long userId = 1L;

        willDoNothing().given(userRepository).deleteById(userId);

        userService.deleteUserById(userId);

        verify(userRepository, times(1))
                .deleteById(userId);
    }

}


