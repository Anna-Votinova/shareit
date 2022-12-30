package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfo;

import javax.validation.ValidationException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {

    @Autowired
    ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    ItemRequestService itemRequestService;

    private ItemRequestDto dto;

    private static final String ITEM_REQUEST_HEADER = "X-Sharer-User-Id";

    @BeforeEach
    public void setUp() {

        dto = ItemRequestDto.builder()
                .id(1L)
                .description("Хотел бы взять в аренду чесалку для кота")
                .created(LocalDateTime.parse("2022-12-18T20:00:57"))
                .items(Collections.emptyList())
                .build();

    }

    @DisplayName("MockMvc test for addNewRequest method")
    @Test
    void givenAnyObject_whenAddNewRequest_thenReturnItemRequestDTO() throws Exception {
        when(itemRequestService.addNewRequest(anyLong(), any())).thenReturn(dto);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(dto))
                        .header(ITEM_REQUEST_HEADER, 1L)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(dto)));


    }

    @DisplayName("MockMvc test for addNewRequest method(negative scenario)")
    @Test
    void givenWrongUserId_whenAddNewRequest_thenThrowsException() throws Exception {

        when(itemRequestService.addNewRequest(anyLong(), any())).thenThrow(IllegalArgumentException.class);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(dto))
                        .header(ITEM_REQUEST_HEADER, 1L)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is(404));


    }

    @DisplayName("MockMvc test for findItemRequestById method")
    @Test
    void givenItemRequestIdAndUserId_whenFindItemRequestById_thenReturnItemRequestDTO() throws Exception {

        when(itemRequestService.findItemRequestById(anyLong(), anyLong())).thenReturn(dto);

        mvc.perform(get("/requests/1")
                        .header(ITEM_REQUEST_HEADER, 1L)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(dto)));

    }

    @DisplayName("MockMvc test for findItemRequestById method(negative scenario)")
    @Test
    void givenWrongUserIdOrItemId_whenFindItemRequestById_thenThrowsException() throws Exception {

        when(itemRequestService.findItemRequestById(anyLong(), any())).thenThrow(IllegalArgumentException.class);

        mvc.perform(get("/requests/1")
                        .header(ITEM_REQUEST_HEADER, -1L)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is(404));


    }

    @DisplayName("MockMvc test for findAllItemRequests method")
    @Test
    void givenUserId_whenFindAllItemRequests_thenReturnItemRequestDTO() throws Exception {

        when(itemRequestService.findAll(anyLong())).thenReturn(List.of(dto));

        mvc.perform(get("/requests")
                        .header(ITEM_REQUEST_HEADER, 1L)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(dto))));

    }

    @DisplayName("MockMvc test for findAllItemRequests method")
    @Test
    void givenUserId_whenFindAllItemRequests_thenReturnItemRequestDTOWithItemInfo() throws Exception {


        ItemRequestInfo info = ItemRequestInfo.builder()
                .id(1L)
                .name("Щетка для кота")
                .description("Щетка для всех пород котов")
                .available(true)
                .requestId(1L)
                .build();

        dto.setItems(List.of(info));

        when(itemRequestService.findAll(anyLong())).thenReturn(List.of(dto));

        mvc.perform(get("/requests")
                        .header(ITEM_REQUEST_HEADER, 1L)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(dto.getId()))
                .andExpect(jsonPath("$[0].description").value(dto.getDescription()))
                .andExpect(jsonPath("$[0].created").value("2022-12-18T20:00:57"))
                .andExpect(jsonPath("$[0].items[0].id").value(dto.getItems().get(0).getId()))
                .andExpect(jsonPath("$[0].items[0].name").value(dto.getItems().get(0).getName()))
                .andExpect(jsonPath("$[0].items[0].description").value(dto.getItems().get(0).getDescription()))
                .andExpect(jsonPath("$[0].items[0].available").value(dto.getItems().get(0).getAvailable()))
                .andExpect(jsonPath("$[0].items[0].requestId").value(dto.getItems().get(0).getRequestId()))
                .andExpect(jsonPath("$[1]").doesNotExist());


    }

    @DisplayName("MockMvc test for findAllItemRequests method")
    @Test
    void givenCorrectArguments_whenFindAllItemRequests_thenReturnEmptyListOfItemRequestsDTO()
            throws Exception {

        when(itemRequestService.findAll(anyLong())).thenReturn(Collections.emptyList());

        mvc.perform(get("/requests")
                        .header(ITEM_REQUEST_HEADER, 1L)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(Collections.emptyList())));

    }

    @DisplayName("MockMvc test for findAllPageable method")
    @Test
    void givenUserId_whenFindAllPageable_thenReturnItemRequestDTO() throws Exception {

        when(itemRequestService.findAllPageable(anyLong(), anyInt(), anyInt())).thenReturn(List.of(dto));

        mvc.perform(get("/requests/all")
                        .header(ITEM_REQUEST_HEADER, 1L)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .param("from", "1")
                        .param("size", "1")
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(dto))));

    }


    @DisplayName("MockMvc test for findAllPageable method")
    @Test
    void givenCorrectArguments_whenFindAllPageable_thenReturnEmptyListOfItemRequestsDTO()
            throws Exception {

        when(itemRequestService.findAllPageable(anyLong(), anyInt(), anyInt())).thenReturn(Collections.emptyList());

        mvc.perform(get("/requests/all")
                        .header(ITEM_REQUEST_HEADER, 1L)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .param("from", "1")
                        .param("size", "1")
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(Collections.emptyList())));

    }

    @DisplayName("MockMvc test for findAllPageable method(negative scenario)")
    @Test
    void givenWrongArguments_whenFindAllPageable_thenThrowException() throws Exception {


        when(itemRequestService.findAllPageable(anyLong(), anyInt(), anyInt()))
                .thenThrow(ValidationException.class);

        mvc.perform(get("/requests/all")
                        .header(ITEM_REQUEST_HEADER, 1L)
                        .param("from", "-1")
                        .param("size", "0")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is(400));

    }

    @DisplayName("MockMvc test for findAllPageable method(negative scenario)")
    @Test
    void givenWrongUserId_whenFindAllPageable_thenThrowException() throws Exception {


        when(itemRequestService.findAllPageable(anyLong(), anyInt(), anyInt()))
                .thenThrow(IllegalArgumentException.class);

        mvc.perform(get("/requests/all")
                        .header(ITEM_REQUEST_HEADER, -1L)
                        .param("from", "1")
                        .param("size", "1")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is(404));

    }


}
