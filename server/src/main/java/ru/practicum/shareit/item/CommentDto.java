package ru.practicum.shareit.item;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

@ToString
@Getter
@Setter
@Builder
public class CommentDto implements Serializable {


    private Long id;
    private String text;
    private String authorName;
    private LocalDateTime created;
}
