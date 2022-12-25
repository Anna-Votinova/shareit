package ru.practicum.shareit.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ItemRequestMapperTest {

    private final ItemRequestMapper itemRequestMapper = new ItemRequestMapper();

    private final ItemRequestDto dto = ItemRequestDto.builder()
            .id(1L)
            .description("Хотел бы взять в аренду чесалку для кота")
            .build();

    private final ItemRequest iReq = ItemRequest.builder()
            .id(1L)
            .description("Хотел бы взять в аренду чесалку для кота")
            .created(Timestamp.valueOf(LocalDateTime.parse("2022-12-18T20:00:57")))
            .build();

    @DisplayName("Test for fromDto method")
    @Test
    void givenItemRequestDto_whenFromDto_thenItemRequestObject() {

        final ItemRequest fromRequest = itemRequestMapper.fromDto(dto);

        assertThat(fromRequest, notNullValue());
        assertThat(fromRequest.getId(), equalTo(1L));
        assertThat(fromRequest.getDescription(), equalTo("Хотел бы взять в аренду чесалку для кота"));
        assertThat(fromRequest.getCreated(), instanceOf(Timestamp.class));

    }

    @DisplayName("Test for toDto method")
    @Test
    void givenItemRequest_whenToDto_thenItemRequestDto() {

        final ItemRequestDto toResponse = itemRequestMapper.toDto(iReq);

        assertThat(toResponse, notNullValue());
        assertThat(toResponse.getId(), equalTo(1L));
        assertThat(toResponse.getDescription(), equalTo("Хотел бы взять в аренду чесалку для кота"));
        assertThat(toResponse.getCreated(), equalTo(iReq.getCreated().toLocalDateTime()));

    }


}
