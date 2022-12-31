package ru.practicum.shareit.user;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody UserDto userDto) {
        return userClient.create(userDto);
    }

    @PatchMapping("{userId}")
    public ResponseEntity<Object> update(@PathVariable @Positive(message = "id не может быть отрицательным числом")
                                    Long userId, @NonNull @RequestBody UserDto userDto) {
        return userClient.update(userId, userDto);
    }

    @GetMapping("{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable @Positive (message = "id не может быть отрицательным числом")
                                         Long userId) {
        return userClient.getUser(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        return userClient.getUsers();
    }

    @DeleteMapping("{userId}")
    public ResponseEntity<Object> deleteUserById(@PathVariable @Positive (message = "id не может быть отрицательным числом")
                               Long userId) {
       return userClient.deleteUser(userId);
    }
}
