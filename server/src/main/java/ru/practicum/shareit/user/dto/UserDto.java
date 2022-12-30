package ru.practicum.shareit.user.dto;

import lombok.*;

import java.io.Serializable;

@ToString
@NonNull
@Getter
@Setter
@AllArgsConstructor
public class UserDto implements Serializable {

    private Long id;
    private String name;
    private String email;
}
