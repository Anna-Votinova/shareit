package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDtoFromRequest;
import ru.practicum.shareit.booking.dto.BookingDtoToResponse;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.exceptions.UnknownStateException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.validation.ValidationException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private final BookingMapper bookingMapper;

    private static final Sort SORT = Sort.by(Sort.Direction.DESC, "start");

    @Override
    public BookingDtoToResponse createBooking(Long userId, BookingDtoFromRequest dto) {

        Item item = itemRepository.findById(dto.getItemId()).orElseThrow(
                () -> new IllegalArgumentException("Item с id " + dto.getItemId() + " не найден"));
        if (!item.getAvailable()) {
            throw new ValidationException("Item с id " + dto.getItemId() + " не доступен для бронирования");
        }

        User user = checkAndReturnUser(userId);

        if (item.getOwner().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Юзер не может забронировать свою же вещь");
        }
        Booking booking = bookingMapper.fromDto(dto);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        return bookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDtoToResponse setApproveToBooking(Long userId, Long bookingId, Boolean approved) {
        User user = checkAndReturnUser(userId);
        Booking booking = checkAndReturnBooking(bookingId);
        Item item = checkAndReturnItem(booking);
        if (!item.getOwner().equals(user)) {
            throw new IllegalArgumentException("Невозможно подтвердить " +
                    "статус бронирования не владельцем вещи");
        }

        if (booking.getStatus() != BookingStatus.APPROVED) {
            if (approved.equals(true)) {
                booking.setStatus(BookingStatus.APPROVED);
            } else {
                booking.setStatus(BookingStatus.REJECTED);
            }
        } else {
            throw new ValidationException("Невозможно подтвердить бронирование. Возможные причины: статус подтвержден");
        }

        return bookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDtoToResponse getBooking(Long userId, Long bookingId) {
        Booking booking = checkAndReturnBooking(bookingId);
        User user = checkAndReturnUser(userId);
        Item item = checkAndReturnItem(booking);
        if (booking.getBooker().equals(user) || item.getOwner().equals(user)) {
            return bookingMapper.toDto(booking);
        }
        throw new IllegalArgumentException("Некорректный запрос, информацию о бронировании " +
                "могут запрашивать только владелец вещи или автор бронирования");
    }

    @Override
    public List<BookingDtoToResponse> getBookingsOfUserAllOrByState(
            Long userId, BookingState state, int from, int size) {

        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("Пользователя " + userId + " не существует");
        }

        Pageable pageable = PageRequest.of(from, size, SORT);
        Page<Booking> bookings;
        Timestamp currentTime = Timestamp.from(Instant.now());


        switch (state) {
            case ALL:
                bookings = bookingRepository
                        .findAll(pageable);
                break;
            case PAST:
                bookings = bookingRepository
                        .findAllByBookerIdAndEndBefore(userId, currentTime, pageable);
                break;
            case CURRENT:
                bookings = bookingRepository
                         .findAllByBookerIdAndStartBeforeAndEndAfter(
                                 userId, currentTime, currentTime, pageable);
                break;
            case FUTURE:
                    bookings = bookingRepository
                         .findAllByBookerIdAndStartAfter(userId, currentTime, pageable);
                break;
            case WAITING:
                    bookings = bookingRepository
                            .findAllByBookerIdAndStatus(
                                    userId, BookingStatus.WAITING, pageable);
                break;
            case REJECTED:
                    bookings = bookingRepository
                            .findAllByBookerIdAndStatus(
                                    userId, BookingStatus.REJECTED, pageable);
                break;
            default:
                throw new UnknownStateException(String.valueOf(state));
            }

        return bookings
                .stream()
                .map(bookingMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDtoToResponse> getBookingsAllOrByStateForEveryUserItem(
            Long userId, BookingState state, int from, int size) {

        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("Пользователя " + userId + " не существует");
        }


        Pageable pageable = PageRequest.of(from, size, SORT);
        Page<Booking> bookingsForItems;

        Timestamp currentTime = Timestamp.from(Instant.now());


        switch (state) {
            case ALL:
                bookingsForItems = bookingRepository.findAllByItemOwnerId(userId, pageable);
                break;
            case PAST:
                bookingsForItems = bookingRepository
                        .findAllByItemOwnerIdAndEndBefore(userId, currentTime, pageable);
                break;
            case CURRENT:
                bookingsForItems = bookingRepository
                        .findAllByItemOwnerIdAndStartBeforeAndEndAfter(
                                userId, currentTime, currentTime, pageable);
                break;
            case FUTURE:
                bookingsForItems = bookingRepository
                        .findAllByItemOwnerIdAndStartAfter(userId, currentTime, pageable);
                break;
            case WAITING:
                bookingsForItems = bookingRepository
                        .findAllByItemOwnerIdAndStatus(userId, BookingStatus.WAITING, pageable);
                break;
            case REJECTED:
                bookingsForItems = bookingRepository
                        .findAllByItemOwnerIdAndStatus(userId, BookingStatus.REJECTED, pageable);
                break;
            default:
                throw new UnknownStateException(String.valueOf(state));
        }

        return bookingsForItems
                .stream()
                .map(bookingMapper::toDto)
                .collect(Collectors.toList());
    }

    private Booking checkAndReturnBooking(Long bookingId) {
        return Optional.of(bookingRepository.findById(bookingId)).get().orElseThrow(
                () -> new IllegalArgumentException("Booking с id " + bookingId + " не найден"));
    }

    private User checkAndReturnUser(Long userId) {
        return Optional.of(userRepository.findById(userId)).get().orElseThrow(
                () -> new IllegalArgumentException("User с id " + userId + " не найден"));
    }

    private Item checkAndReturnItem(Booking booking) {
        return Optional.of(itemRepository.findById(booking.getItem().getId())).get().orElseThrow(
                () -> new IllegalArgumentException("Item с id " + booking.getItem().getId() + " не найден"));
    }
}
