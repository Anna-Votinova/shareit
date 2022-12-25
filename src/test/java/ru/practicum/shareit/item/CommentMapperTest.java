package ru.practicum.shareit.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.model.User;

import java.sql.Timestamp;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class CommentMapperTest {

    private final CommentMapper commentMapper = new CommentMapper();


    private final User booker = new User(3L,"Alla","alla@36on.ru");

    private final CommentDto commentDto = CommentDto.builder()
            .text("Хорошая щетка, моему коту подошла")
            .build();

    private Comment comment = new Comment(1L, "Хорошая щетка, моему коту подошла");

    @DisplayName("Test for fromDto method")
    @Test
    void givenCommentDto_whenFromDto_thenCommentObject() {

        final Comment fromRequest = commentMapper.fromDto(commentDto);

        assertThat(fromRequest, notNullValue());
        assertThat(fromRequest.getId(), nullValue());
        assertThat(fromRequest.getText(), equalTo("Хорошая щетка, моему коту подошла"));

    }

    @DisplayName("Test for toDto method")
    @Test
    void givenComment_whenToDto_thenCommentDto() {

        comment.setCreated(Timestamp.valueOf("2022-11-13 00:03:04"));
        comment.setAuthor(booker);

        final CommentDto toResponse = commentMapper.toDto(comment);

        assertThat(toResponse, notNullValue());
        assertThat(toResponse.getId(), equalTo(1L));
        assertThat(toResponse.getText(), equalTo("Хорошая щетка, моему коту подошла"));
        assertThat(toResponse.getAuthorName(), equalTo("Alla"));
        assertThat(toResponse.getCreated(), equalTo(comment.getCreated().toLocalDateTime()));



    }


}
