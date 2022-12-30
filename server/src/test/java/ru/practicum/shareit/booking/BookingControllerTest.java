package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.dto.BookingDtoFromRequest;
import ru.practicum.shareit.booking.dto.BookingDtoToResponse;
import ru.practicum.shareit.booking.dto.ItemDtoBookingToResponse;
import ru.practicum.shareit.booking.dto.UserDtoBookingToResponse;
import ru.practicum.shareit.exceptions.UnknownStateException;

import javax.validation.ValidationException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {

    @Autowired
    ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    BookingService bookingService;

    private BookingDtoFromRequest requestDto;

    private BookingDtoToResponse responseDto;

    private UserDtoBookingToResponse booker;

    private ItemDtoBookingToResponse item;



    private static final String BOOKING_HEADER = "X-Sharer-User-Id";

    @BeforeEach
    public void setUp() {

        booker = UserDtoBookingToResponse.builder().id(1L).build();
        item = ItemDtoBookingToResponse.builder().id(1L).name("Щетка для кота").build();

        requestDto = new BookingDtoFromRequest(
                1L,
                LocalDateTime.parse("2023-11-12T10:09:01"),
                LocalDateTime.parse("2023-12-13T10:09:01"));

        responseDto = new BookingDtoToResponse(
                1L,
                LocalDateTime.parse("2023-11-12T10:09:01"),
                LocalDateTime.parse("2023-12-13T10:09:01"),
                BookingStatus.WAITING,
                booker,
                item
        );

    }

    @DisplayName("MockMvc test for createBooking method")
    @Test
    void givenAnyObject_whenCreateBooking_thenReturnBookingDTO() throws Exception {
        when(bookingService.createBooking(anyLong(), any())).thenReturn(responseDto);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(requestDto))
                        .header(BOOKING_HEADER, 1L)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(responseDto)));

    }

    @DisplayName("MockMvc test for createBooking method(negative scenario)")
    @Test
    void givenBookingWithWrongStartOrEnd_whenCreateBooking_thenThrowsException() throws Exception {

        when(bookingService.createBooking(anyLong(), any())).thenThrow(ValidationException.class);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(requestDto))
                        .header(BOOKING_HEADER, 1L)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is(400));


    }

    @DisplayName("MockMvc test for createBooking method(negative scenario)")
    @Test
    void givenBookingWithWrongItemId_whenCreateBooking_thenThrowsException() throws Exception {

        when(bookingService.createBooking(anyLong(), any())).thenThrow(IllegalArgumentException.class);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(requestDto))
                        .header(BOOKING_HEADER, 1L)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is(404));


    }

    @DisplayName("MockMvc test for setApproveToBooking method")
    @Test
    void givenBookingId_whenSetApproveToBooking_thenReturnBookingDTO() throws Exception {

       BookingDtoToResponse responseDto1 = new BookingDtoToResponse(
                1L,
                LocalDateTime.parse("2023-11-12T10:09:01"),
                LocalDateTime.parse("2023-12-13T10:09:01"),
                BookingStatus.APPROVED,
                booker,
                item);

        when(bookingService.setApproveToBooking(anyLong(), any(), anyBoolean())).thenReturn(responseDto1);

        mvc.perform(patch("/bookings/1")
                        .param("approved", "true")
                        .header(BOOKING_HEADER, 1L)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responseDto1.getId()))
                .andExpect(jsonPath("$.start").value("2023-11-12T10:09:01"))
                .andExpect(jsonPath("$.end").value("2023-12-13T10:09:01"))
                .andExpect(jsonPath("$.status").value(BookingStatus.APPROVED.toString()))
                .andExpect(jsonPath("$.booker.id").value(responseDto1.getBooker().getId()))
                .andExpect(jsonPath("$.item.id").value(responseDto1.getItem().getId()))
                .andExpect(jsonPath("$.item.name").value(responseDto1.getItem().getName()));

    }

    @DisplayName("MockMvc test for setApproveToBooking method(negative scenario)")
    @Test
    void givenBookingIdWithWrongArguments_whenSetApproveToBooking_thenThrowException() throws Exception {


        when(bookingService.setApproveToBooking(anyLong(), any(), anyBoolean()))
                .thenThrow(IllegalArgumentException.class);

        mvc.perform(patch("/bookings/1")
                        .param("approved", "true")
                        .header(BOOKING_HEADER, 1L)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is(404));

    }

    @DisplayName("MockMvc test for setApproveToBooking method(negative scenario)")
    @Test
    void givenBookingIdWithApprovedStatus_whenSetApproveToBooking_thenThrowException() throws Exception {


        when(bookingService.setApproveToBooking(anyLong(), any(), anyBoolean()))
                .thenThrow(ValidationException.class);

        mvc.perform(patch("/bookings/1")
                        .param("approved", "true")
                        .header(BOOKING_HEADER, 1L)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is(400));

    }

    @DisplayName("MockMvc test for getBooking method")
    @Test
    void givenBookingIdAndUserId_whenGetBooking_thenReturnBookingDTO() throws Exception {

        when(bookingService.getBooking(anyLong(), anyLong())).thenReturn(responseDto);

        mvc.perform(get("/bookings/1")
                        .header(BOOKING_HEADER, 1L)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(responseDto)));

    }

    @DisplayName("MockMvc test for getBooking method(negative scenario)")
    @Test
    void givenBookingIdAndUserIdWithWrongArguments_whenGetBooking_thenThrowException() throws Exception {


        when(bookingService.getBooking(anyLong(), anyLong()))
                .thenThrow(IllegalArgumentException.class);

        mvc.perform(get("/bookings/1")
                        .header(BOOKING_HEADER, 1L)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is(404));

    }

    @DisplayName("MockMvc test for getBookingsOfUserAllOrByState method")
    @Test
    void givenUserIdAndOtherCorrectArguments_whenGetBookingsOfUserAllOrByState_thenReturnListOfBookingDTO()
            throws Exception {

        when(bookingService.getBookingsOfUserAllOrByState(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(responseDto));

        mvc.perform(get("/bookings")
                        .header(BOOKING_HEADER, 1L)
                        .param("state", "ALL")
                        .param("from", "1")
                        .param("size", "1")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(responseDto))));

    }

    @DisplayName("MockMvc test for getBookingsOfUserAllOrByState method")
    @Test
    void givenUserIdAndOtherCorrectArguments_whenGetBookingsOfUserAllOrByState_thenReturnEmptyListOfBookingDTO()
            throws Exception {

        when(bookingService.getBookingsOfUserAllOrByState(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(Collections.emptyList());

        mvc.perform(get("/bookings")
                        .header(BOOKING_HEADER, 1L)
                        .param("state", "ALL")
                        .param("from", "1")
                        .param("size", "1")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(Collections.emptyList())));

    }

    @DisplayName("MockMvc test for getBookingsOfUserAllOrByState method(negative scenario)")
    @Test
    void givenWrongArguments_whenGetBookingsOfUserAllOrByState_thenThrowException() throws Exception {


        when(bookingService.getBookingsOfUserAllOrByState(anyLong(), any(), anyInt(), anyInt()))
                .thenThrow(ValidationException.class);

        mvc.perform(get("/bookings")
                        .header(BOOKING_HEADER, 1L)
                        .param("state", "ALL")
                        .param("from", "-1")
                        .param("size", "0")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is(400));

    }

    @DisplayName("MockMvc test for getBookingsOfUserAllOrByState method(negative scenario)")
    @Test
    void givenWrongUserId_whenGetBookingsOfUserAllOrByState_thenThrowException() throws Exception {


        when(bookingService.getBookingsOfUserAllOrByState(anyLong(), any(), anyInt(), anyInt()))
                .thenThrow(IllegalArgumentException.class);

        mvc.perform(get("/bookings")
                        .header(BOOKING_HEADER, -1L)
                        .param("state", "ALL")
                        .param("from", "1")
                        .param("size", "1")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is(404));

    }

    @DisplayName("MockMvc test for getBookingsOfUserAllOrByState method(negative scenario)")
    @Test
    void givenWrongState_whenGetBookingsOfUserAllOrByState_thenThrowException() throws Exception {


        when(bookingService.getBookingsOfUserAllOrByState(anyLong(), any(), anyInt(), anyInt()))
                .thenThrow(UnknownStateException.class);

        mvc.perform(get("/bookings")
                        .header(BOOKING_HEADER, 1L)
                        .param("state", BookingState.UNSUPPORTED_STATUS.toString())
                        .param("from", "1")
                        .param("size", "1")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is(500));

    }

    @DisplayName("MockMvc test for getBookingsAllOrByStateForOwner method")
    @Test
    void givenUserIdAndOtherCorrectArguments_whenGetBookingsAllOrByStateForOwner_thenReturnListOfBookingDTO()
            throws Exception {

        when(bookingService.getBookingsAllOrByStateForEveryUserItem(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(responseDto));

        mvc.perform(get("/bookings/owner")
                        .header(BOOKING_HEADER, 1L)
                        .param("state", "ALL")
                        .param("from", "1")
                        .param("size", "1")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(responseDto))));

    }

    @DisplayName("MockMvc test for getBookingsAllOrByStateForOwner method")
    @Test
    void givenUserIdAndOtherCorrectArguments_whenGetBookingsAllOrByStateForOwner_thenReturnEmptyListOfBookingDTO()
            throws Exception {

        when(bookingService.getBookingsAllOrByStateForEveryUserItem(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(Collections.emptyList());

        mvc.perform(get("/bookings/owner")
                        .header(BOOKING_HEADER, 1L)
                        .param("state", "ALL")
                        .param("from", "1")
                        .param("size", "1")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(Collections.emptyList())));

    }

    @DisplayName("MockMvc test for getBookingsAllOrByStateForOwner method(negative scenario)")
    @Test
    void givenWrongArguments_whenGetBookingsAllOrByStateForOwner_thenThrowException() throws Exception {


        when(bookingService.getBookingsAllOrByStateForEveryUserItem(anyLong(), any(), anyInt(), anyInt()))
                .thenThrow(ValidationException.class);

        mvc.perform(get("/bookings/owner")
                        .header(BOOKING_HEADER, 1L)
                        .param("state", "ALL")
                        .param("from", "-1")
                        .param("size", "0")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is(400));

    }

    @DisplayName("MockMvc test for getBookingsAllOrByStateForOwner method(negative scenario)")
    @Test
    void givenWrongState_whenGetBookingsAllOrByStateForOwner_thenThrowException() throws Exception {


        when(bookingService.getBookingsAllOrByStateForEveryUserItem(anyLong(), any(), anyInt(), anyInt()))
                .thenThrow(UnknownStateException.class);

        mvc.perform(get("/bookings/owner")
                        .header(BOOKING_HEADER, 1L)
                        .param("state", BookingState.UNSUPPORTED_STATUS.toString())
                        .param("from", "1")
                        .param("size", "1")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is(500));

    }


}
