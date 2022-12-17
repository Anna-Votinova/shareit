package ru.practicum.shareit.user.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@ToString
@NonNull
@Getter
@Setter
@AllArgsConstructor
public class UserDto implements Serializable {

    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    @Email
    private String email;
}
