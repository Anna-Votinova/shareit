package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDateTime;

@ToString
@Getter
@Setter
@Builder
public class CommentDto implements Serializable {


    private Long id;
    @NotBlank
    private String text;
    private String authorName;
    private LocalDateTime created;
}
