package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoFromRequest;
import ru.practicum.shareit.booking.dto.BookingDtoToResponse;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDtoToResponse createBooking(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                              @Valid @RequestBody BookingDtoFromRequest dto) {
        return bookingService.createBooking(userId, dto);
    }

    @PatchMapping("{bookingId}")
    public BookingDtoToResponse setApproveToBooking(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @Positive @PathVariable Long bookingId,
                                                    @RequestParam(value = "approved") final Boolean approved) {
        return bookingService.setApproveToBooking(userId, bookingId, approved);
    }

    @GetMapping("{bookingId}")
    public BookingDtoToResponse getBooking(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                           @Positive @PathVariable Long bookingId) {
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public List<BookingDtoToResponse> getBookingsAllOrByState(
            @Positive @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(value = "state", defaultValue = "ALL", required = false) final BookingState state,
            @PositiveOrZero @RequestParam(defaultValue = "0") final int from,
            @Positive @RequestParam(defaultValue = "10") final int size) {
        return bookingService.getBookingsOfUserAllOrByState(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDtoToResponse> getBookingsAllOrByStateForOwner(
            @Positive @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(value = "state", defaultValue = "ALL", required = false) final BookingState state,
            @PositiveOrZero @RequestParam(defaultValue = "0") final int from,
            @Positive @RequestParam(defaultValue = "10") final int size) {
        return bookingService.getBookingsAllOrByStateForEveryUserItem(userId, state, from, size);
    }
}
