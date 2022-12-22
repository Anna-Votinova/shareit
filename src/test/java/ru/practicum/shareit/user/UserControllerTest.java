package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    UserService userService;

    @Autowired
    private MockMvc mvc;

    private UserDto userDto;

    @BeforeEach
    public void setUp() {

        userDto = new UserDto(
                1L,
                "Anna",
                "anna13@36on.ru"
        );

    }

    @DisplayName("MockMvc test for create method")
    @Test
    void givenAnyObject_whenCreateUser_thenReturnUserDTO() throws Exception {
        when(userService.create(any()))
                .thenReturn(userDto);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @DisplayName("MockMvc test for create method(negative scenario)")
    @Test
    void givenUserWithExistingEmail_whenSaveUser_thenThrowsException() throws Exception {
        when(userService.create(any()))
                .thenThrow(RuntimeException.class);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(500));
    }

    @DisplayName("MockMvc test for getAllUsers method")
    @Test
    public void givenUserList_whenGetAllUsers_thenReturnUserDtoList() throws Exception {

        UserDto userDto1 = new UserDto(2L, "Olga", "olga@gmail.com");

        when(userService.getAll())
                .thenReturn(List.of(userDto, userDto1));

        mvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(userDto, userDto1))));

    }

    @DisplayName("MockMvc test for getAllUsers (negative scenario)")
    @Test
    public void givenEmptyUserList_whenGetAllUsers_thenThrowsException() throws Exception {

        when(userService.getAll())
                .thenThrow(IllegalArgumentException.class);

        mvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));

    }

    @DisplayName("MockMvc test for getUserById method")
    @Test
    public void givenUserDtoId_whenGetUserById_thenReturnUserObject() throws Exception {

        when(userService.getUserById(anyLong()))
                .thenReturn(Optional.of(userDto));

        mvc.perform(get("/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())))
                .andDo(MockMvcResultHandlers.print());

    }

    @DisplayName("MockMvc test for update method")
    @Test
    public void givenUserObject_whenUpdateUser_thenReturnUpdatedUser() throws Exception {

        userDto.setName("Olga");
        userDto.setEmail("olga@gmail.com");
        when(userService.update(any(), any()))
                .thenReturn(Optional.of(userDto));


        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("Olga"))
                .andExpect(jsonPath("$.email").value("olga@gmail.com"))
                .andDo(MockMvcResultHandlers.print());

    }

    @DisplayName("MockMvc test for deleteUserById method")
    @Test
    public void givenUserId_whenDeleteUser_thenNothing() throws Exception {

        mvc.perform(delete("/users/1"))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteUserById(anyLong());

    }

}
