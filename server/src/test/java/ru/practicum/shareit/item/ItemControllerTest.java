package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.ValidationException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {

    @Autowired
    ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    ItemService itemService;

    private ItemDto dto;

    private static final String ITEM_HEADER = "X-Sharer-User-Id";

    @BeforeEach
    public void setUp() {

        dto = ItemDto.builder()
                .id(1L)
                .name("Щетка для кота")
                .description("Щетка для всех пород котов")
                .available(true)
                .lastBooking(null)
                .nextBooking(null)
                .requestId(1L)
                .comments(Collections.emptySet())
                .build();

    }

    @DisplayName("MockMvc test for addItemToUser method")
    @Test
    void givenAnyObject_whenAddItemToUser_thenReturnItemDTO() throws Exception {
        when(itemService.create(anyLong(), any())).thenReturn(dto);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(dto))
                        .header(ITEM_HEADER, 1L)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(dto)));

    }

    @DisplayName("MockMvc test for addNewRequest method(negative scenario)")
    @Test
    void givenWrongUserIdOrItemDtoWithWrongId_whenAddItemToUser_thenThrowsException() throws Exception {

        when(itemService.create(anyLong(), any())).thenThrow(IllegalArgumentException.class);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(dto))
                        .header(ITEM_HEADER, -1L)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is(404));


    }

    @DisplayName("MockMvc test for getItemById method")
    @Test
    void givenItemIdAndUserId_whenGetItemById_thenReturnItemDTO() throws Exception {

        when(itemService.getItemByIdForAllUser(anyLong(), anyLong())).thenReturn(Optional.ofNullable(dto));

        mvc.perform(get("/items/1")
                        .header(ITEM_HEADER, 1L)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(dto)));

    }

    @DisplayName("MockMvc test for getItemById method")
    @Test
    void givenWrongUserIdOrItemDtoWithWrongId_whenGetItemById_thenThrowsException() throws Exception {
        when(itemService.getItemByIdForAllUser(anyLong(), anyLong())).thenThrow(IllegalArgumentException.class);

        mvc.perform(get("/items/-1")
                        .header(ITEM_HEADER, 1L)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is(404));

    }

    @DisplayName("MockMvc test for getAllItemsOfUser method")
    @Test
    void givenUserId_whenGetAllItemsOfUser_thenReturnListOfItemDTO() throws Exception {

        when(itemService.findAllPageable(anyLong(), anyInt(), anyInt())).thenReturn(List.of(dto));

        mvc.perform(get("/items")
                        .header(ITEM_HEADER, 1L)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .param("from", "1")
                        .param("size", "1")
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(dto))));

    }


    @DisplayName("MockMvc test for getAllItemsOfUser method")
    @Test
    void givenUserId_whenGetAllItemsOfUser_thenReturnListOfItemDtoWithCommentsAndLastNextBooking()
            throws Exception {

        ItemDtoLastNextBooking last = ItemDtoLastNextBooking.builder().id(5L).bookerId(5L).build();
        ItemDtoLastNextBooking next = ItemDtoLastNextBooking.builder().id(6L).bookerId(6L).build();
        Set<CommentDto> comments = new HashSet<>(Set.of(CommentDto.builder()
                .id(1L).text("Отл").authorName("Илья")
                .created(LocalDateTime.parse("2022-12-18T20:00:57")).build()));

        dto.setComments(comments);
        dto.setLastBooking(last);
        dto.setNextBooking(next);

        when(itemService.findAllPageable(anyLong(), anyInt(), anyInt())).thenReturn(List.of(dto));

        mvc.perform(get("/items")
                        .header(ITEM_HEADER, 1L)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .param("from", "1")
                        .param("size", "1")
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(dto.getId()))
                .andExpect(jsonPath("$[0].name").value(dto.getName()))
                .andExpect(jsonPath("$[0].description").value(dto.getDescription()))
                .andExpect(jsonPath("$[0].available").value(dto.getAvailable()))
                .andExpect(jsonPath("$[0].lastBooking.bookerId").value(dto.getLastBooking().getBookerId()))
                .andExpect(jsonPath("$[0].lastBooking.id").value(dto.getLastBooking().getId()))
                .andExpect(jsonPath("$[0].nextBooking.bookerId").value(dto.getNextBooking().getBookerId()))
                .andExpect(jsonPath("$[0].nextBooking.id").value(dto.getNextBooking().getId()))
                .andExpect(jsonPath("$[0].requestId").value(dto.getRequestId()))
                .andExpect(jsonPath("$[0].comments[0].id").value(1))
                .andExpect(jsonPath("$[0].comments[0].text").value("Отл"))
                .andExpect(jsonPath("$[0].comments[0].authorName").value("Илья"))
                .andExpect(jsonPath("$[0].comments[0].created").value("2022-12-18T20:00:57"))
                .andExpect(jsonPath("$[1]").doesNotExist())
                .andExpect(jsonPath("$[0].comments[1]").doesNotExist());

    }

    @DisplayName("MockMvc test for getAllItemsOfUser method(negative scenario)")
    @Test
    void givenWrongSizeAndFrom_whenGetAllItemsOfUser_thenThrowException() throws Exception {


        when(itemService.findAllPageable(anyLong(), anyInt(), anyInt()))
                .thenThrow(ValidationException.class);

        mvc.perform(get("/items")
                        .header(ITEM_HEADER, 1L)
                        .param("from", "-1")
                        .param("size", "0")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is(400));

    }

    @DisplayName("MockMvc test for getAllItemsOfUser method(negative scenario)")
    @Test
    void givenWrongUserId_whenGetAllItemsOfUser_thenThrowException() throws Exception {


        when(itemService.findAllPageable(anyLong(), anyInt(), anyInt()))
                .thenThrow(IllegalArgumentException.class);

        mvc.perform(get("/items")
                        .header(ITEM_HEADER, -1L)
                        .param("from", "1")
                        .param("size", "1")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is(404));

    }

    @DisplayName("MockMvc test for getItemByText method")
    @Test
    void givenText_whenGetItemByText_thenReturnListOfItemDTO() throws Exception {

        when(itemService.findItemByText(anyString(), anyInt(), anyInt())).thenReturn(List.of(dto));

        mvc.perform(get("/items/search")
                        .param("text", "Щетка для кота")
                        .param("from", "1")
                        .param("size", "1")
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(content().json(mapper.writeValueAsString(List.of(dto))));

    }

    @DisplayName("MockMvc test forgetItemByText method(negative scenario)")
    @Test
    void givenWrongSizeAndFrom_whenGetItemByText_thenThrowException() throws Exception {


        when(itemService.findItemByText(anyString(), anyInt(), anyInt()))
                .thenThrow(ValidationException.class);

        mvc.perform(get("/items/search")
                        .param("text", "Щетка для кота")
                        .param("from", "-1")
                        .param("size", "0")
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is(400));

    }

    @DisplayName("MockMvc test for getItemByText method")
    @Test
    void givenCorrectArguments_whenGetItemByText_thenReturnEmptyListOfItemDTO()
            throws Exception {

        when(itemService.findItemByText(anyString(), anyInt(), anyInt())).thenReturn(Collections.emptyList());

        mvc.perform(get("/items/search")
                        .param("text", " ")
                        .param("from", "1")
                        .param("size", "1")
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)))
                .andExpect(content().json(mapper.writeValueAsString(Collections.emptyList())));

    }

    @DisplayName("MockMvc test for updateItem method")
    @Test
    public void givenItemDto_whenUpdateItem_thenReturnUpdatedItemDto() throws Exception {

        ItemDto dto2 = ItemDto.builder()
                .id(1L)
                .name("Самая лучшая щетка для котов!")
                .description("Щетка для всех пород котов и кошек")
                .available(true)
                .lastBooking(null)
                .nextBooking(null)
                .requestId(1L)
                .comments(Collections.emptySet())
                .build();


        when(itemService.update(anyLong(), anyLong(), any()))
                .thenReturn(Optional.ofNullable(dto2));


        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(dto2))
                        .header(ITEM_HEADER, 1L)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(dto2)));

    }

    @DisplayName("MockMvc test for updateItem method(negative scenario)")
    @Test
    void givenWrongUserAndItemId_whenUpdateItem_thenThrowException() throws Exception {


        when(itemService.update(anyLong(), anyLong(), any()))
                .thenThrow(IllegalArgumentException.class);

        mvc.perform(patch("/items/100")
                        .content(mapper.writeValueAsString(dto))
                        .header(ITEM_HEADER, 100L)
                        .param("from", "1")
                        .param("size", "1")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is(404));

    }

    @DisplayName("MockMvc test for addItemToUser method")
    @Test
    void givenAnyObject_whenAddCommentToItem_thenReturnCommentDTO() throws Exception {

        CommentDto commentDto = CommentDto.builder()
                .id(1L).text("Отл").authorName("Илья")
                .created(LocalDateTime.parse("2022-12-18T20:00:57")).build();

        when(itemService.addCommentToItem(anyLong(), anyLong(), any())).thenReturn(commentDto);

        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(commentDto))
                        .header(ITEM_HEADER, 1L)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(commentDto)));

    }

    @DisplayName("MockMvc test for addCommentToItem method(negative scenario)")
    @Test
    void givenWrongUserAndItemId_whenAddCommentToItem_thenThrowException() throws Exception {

        CommentDto commentDto = CommentDto.builder()
                .id(1L).text("Отл").authorName("Илья")
                .created(LocalDateTime.parse("2022-12-18T20:00:57")).build();


        when(itemService.addCommentToItem(anyLong(), anyLong(), any()))
                .thenThrow(IllegalArgumentException.class);

        mvc.perform(post("/items/-100/comment")
                        .content(mapper.writeValueAsString(commentDto))
                        .header(ITEM_HEADER, 100L)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is(404));

    }

    @DisplayName("MockMvc test for addCommentToItem method(negative scenario)")
    @Test
    void givenUserWhoHasNotYetBookedTheItem_whenAddCommentToItem_thenThrowException() throws Exception {

        CommentDto commentDto = CommentDto.builder()
                .id(1L).text("Отл").authorName("Илья")
                .created(LocalDateTime.parse("2022-12-18T20:00:57")).build();


        when(itemService.addCommentToItem(anyLong(), anyLong(), any()))
                .thenThrow(ValidationException.class);

        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(commentDto))
                        .header(ITEM_HEADER, 1L)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is(400));

    }


}
