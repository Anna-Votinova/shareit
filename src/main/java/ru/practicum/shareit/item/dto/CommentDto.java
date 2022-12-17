package ru.practicum.shareit.item.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.time.LocalDateTime;

@ToString
@Getter
@Setter
@Builder
public class CommentDto implements Serializable {


    private Long id;
    @NotBlank
    @NotEmpty
    private String text;
    private String authorName;
    private LocalDateTime created;
}
