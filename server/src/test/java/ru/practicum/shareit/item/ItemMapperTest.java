package ru.practicum.shareit.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ItemMapperTest {

    private final ItemMapper itemMapper = new ItemMapper();

    private final Item item1 = new Item(1L, "Вещь", "Хорошая вещь", true);

    private final ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("Вещь")
            .description("Хорошая вещь")
            .available(true)
            .build();

    @DisplayName("Test for fromDto method")
    @Test
    void givenItemDto_whenFromDto_thenItemObject() {

        final Item fromRequest = itemMapper.fromDto(itemDto);

        assertThat(fromRequest, notNullValue());
        assertThat(fromRequest.getId(), equalTo(1L));
        assertThat(fromRequest.getName(), equalTo("Вещь"));
        assertThat(fromRequest.getDescription(), equalTo("Хорошая вещь"));
        assertThat(fromRequest.getAvailable(), equalTo(true));
    }

    @DisplayName("Test for fromItem method")
    @Test
    void givenItem_whenFromItem_thenItemDto() {

        final ItemDto toResponse = itemMapper.fromItem(item1);

        assertThat(toResponse, notNullValue());
        assertThat(toResponse.getId(), equalTo(1L));
        assertThat(toResponse.getName(), equalTo("Вещь"));
        assertThat(toResponse.getDescription(), equalTo("Хорошая вещь"));
        assertThat(toResponse.getRequestId(), nullValue());
        assertThat(toResponse.getAvailable(), equalTo(true));

    }
}
