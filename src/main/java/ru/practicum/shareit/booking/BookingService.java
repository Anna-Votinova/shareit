package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDtoFromRequest;
import ru.practicum.shareit.booking.dto.BookingDtoToResponse;

import java.util.List;


public interface BookingService {

    BookingDtoToResponse createBooking(Long userId, BookingDtoFromRequest dto);

    BookingDtoToResponse setApproveToBooking(Long userId, Long bookingId, Boolean approved);

    BookingDtoToResponse getBooking(Long userId, Long bookingId);

    List<BookingDtoToResponse> getBookingsOfUserAllOrByState(Long userId, BookingState state);

    List<BookingDtoToResponse> getBookingsAllOrByStateForEveryUserItem(Long userId, BookingState state);

}
