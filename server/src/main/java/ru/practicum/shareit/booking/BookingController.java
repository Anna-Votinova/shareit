package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoFromRequest;
import ru.practicum.shareit.booking.dto.BookingDtoToResponse;

import java.util.List;

import static ru.practicum.shareit.Constants.USER_ID;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDtoToResponse createBooking(@RequestHeader(USER_ID) Long userId,
                                              @RequestBody BookingDtoFromRequest dto) {
        return bookingService.createBooking(userId, dto);
    }

    @GetMapping("{bookingId}")
    public BookingDtoToResponse getBooking(@RequestHeader(USER_ID) Long userId,
                                           @PathVariable Long bookingId) {
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public List<BookingDtoToResponse> getBookingsAllOrByState(
            @RequestHeader(USER_ID) Long userId,
            @RequestParam(value = "state", defaultValue = "ALL", required = false) final BookingState state,
            @RequestParam(defaultValue = "0") final int from,
            @RequestParam(defaultValue = "10") final int size) {
        return bookingService.getBookingsOfUserAllOrByState(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDtoToResponse> getBookingsAllOrByStateForOwner(
            @RequestHeader(USER_ID) Long userId,
            @RequestParam(value = "state", defaultValue = "ALL", required = false) final BookingState state,
            @RequestParam(defaultValue = "0") final int from,
            @RequestParam(defaultValue = "10") final int size) {
        return bookingService.getBookingsAllOrByStateForEveryUserItem(userId, state, from, size);
    }

    @PatchMapping("{bookingId}")
    public BookingDtoToResponse setApproveToBooking(@RequestHeader(USER_ID) Long userId,
                                                    @PathVariable Long bookingId,
                                                    @RequestParam(value = "approved") final Boolean approved) {
        return bookingService.setApproveToBooking(userId, bookingId, approved);
    }
}
