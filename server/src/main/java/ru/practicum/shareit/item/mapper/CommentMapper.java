package ru.practicum.shareit.item.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.CommentDto;
import ru.practicum.shareit.item.model.Comment;

@Component
@RequiredArgsConstructor
public class CommentMapper {

    public Comment fromDto(CommentDto dto) {
        return new Comment(
                dto.getId(),
                dto.getText()
        );
    }

    public CommentDto toDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated().toLocalDateTime())
                .build();
    }
}
