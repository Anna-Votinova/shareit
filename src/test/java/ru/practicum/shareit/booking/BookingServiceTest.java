package ru.practicum.shareit.booking;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.*;
import ru.practicum.shareit.exceptions.UnknownStateException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.validation.ValidationException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private static final Sort SORT = Sort.by(Sort.Direction.DESC, "start");

    private Item item1;

    private User user1;
    private User user2;
    private Booking booking1;
    private BookingDtoFromRequest requestDto1;
    private BookingDtoToResponse responseDto1;


    @BeforeEach
    public void setUp() {

        user1 = new User(1L, "Anna", "anna13@36on.ru");
        user2 = new User(2L, "Olga", "olga@gmail.com");


        item1 = new Item(1L, "Вещь", "Хорошая вещь", true);
        item1.setOwner(user1);

        booking1 = new Booking(1L, Timestamp.valueOf("2022-11-12 10:09:00"),
                Timestamp.valueOf("2022-12-13 10:09:00"), item1, user2, BookingStatus.WAITING);

        requestDto1 = new BookingDtoFromRequest();
        requestDto1.setItemId(1L);
        requestDto1.setStart(LocalDateTime.parse("2022-11-12T10:09:00"));
        requestDto1.setEnd(LocalDateTime.parse("2022-12-13T10:09:00"));

        responseDto1 = new BookingDtoToResponse(
                1L,
                LocalDateTime.parse("2022-11-12T10:09:00"),
                LocalDateTime.parse("2022-12-13T10:09:00"),
                BookingStatus.WAITING,
                UserDtoBookingToResponse.builder().id(2L).build(),
                ItemDtoBookingToResponse.builder().id(1L).name("Вещь").build()

        );

    }

    @AfterEach
    public void verifyInteractions() {
        verifyNoMoreInteractions(
                userRepository,
                itemRepository,
                bookingRepository
        );
    }

    @DisplayName("JUnit test for createBooking method")
    @Test
    public void givenUserIdAndBookingDto_whenCreateItem_thenReturnBookingDto() {
        booking1 = new Booking(1L, Timestamp.valueOf("2022-11-12 10:09:00"),
                Timestamp.valueOf("2022-12-13 10:09:00"), null, null, null);
        given(itemRepository.findById(anyLong())).willReturn(Optional.of(item1));
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user2));
        given(bookingMapper.fromDto(requestDto1)).willReturn(booking1);
        given(bookingRepository.save(booking1)).willReturn(booking1);
        given(bookingMapper.toDto(booking1)).willReturn(responseDto1);

        BookingDtoToResponse resp = bookingService.createBooking(user2.getId(), requestDto1);

        assertThat(resp).isNotNull();
        assertThat(resp).isEqualTo(responseDto1);

        verify(itemRepository, times(1))
                .findById(any());
        verify(userRepository, times(1))
                .findById(any());
        verify(bookingRepository, times(1))
                .save(any());


    }

    @DisplayName("JUnit test for createBooking method (negative scenario)")
    @Test
    public void givenUserIdAndBookingDtoWithIncorrectStartAndEnd_whenCreateItem_thenThrowException() {

        requestDto1.setStart(LocalDateTime.parse("2022-12-13T10:09:00"));
        requestDto1.setEnd(LocalDateTime.parse("2022-11-12T10:09:00"));

        assertThrows(ValidationException.class, () -> bookingService.createBooking(user2.getId(), requestDto1));

        verify(itemRepository, times(0))
                .findById(any());
        verify(userRepository, times(0))
                .findById(any());
        verify(bookingRepository, times(0))
                .save(any());


    }

    @DisplayName("JUnit test for createBooking method (negative scenario)")
    @Test
    public void givenUserIdAndBookingDtoWithEqualStartAndEnd_whenCreateItem_thenThrowException() {

        requestDto1.setStart(LocalDateTime.parse("2022-11-12T10:09:00"));
        requestDto1.setEnd(LocalDateTime.parse("2022-11-12T10:09:00"));

        assertThrows(ValidationException.class, () -> bookingService.createBooking(user2.getId(), requestDto1));

        verify(itemRepository, times(0))
                .findById(any());
        verify(userRepository, times(0))
                .findById(any());
        verify(bookingRepository, times(0))
                .save(any());

    }

    @DisplayName("JUnit test for createBooking method (negative scenario)")
    @Test
    public void givenItemIdWrong_whenCreateItem_thenThrowException() {

        given(itemRepository.findById(anyLong())).willThrow(IllegalArgumentException.class);

        assertThrows(IllegalArgumentException.class, () -> bookingService.createBooking(user1.getId(), requestDto1));

        verify(itemRepository, times(1))
                .findById(any());
        verify(userRepository, times(0))
                .findById(any());

        verify(bookingRepository, times(0))
                .save(any());
    }

    @DisplayName("JUnit test for createBooking method (negative scenario)")
    @Test
    public void givenUserIdAndBookingDtoNotAvailable_whenCreateItem_thenThrowException() {

        given(itemRepository.findById(anyLong())).willReturn(Optional.of(item1));
        item1.setAvailable(false);

        assertThrows(ValidationException.class, () -> bookingService.createBooking(user2.getId(), requestDto1));

        verify(itemRepository, times(1))
                .findById(any());
        verify(userRepository, times(0))
                .findById(any());

        verify(bookingRepository, times(0))
                .save(any());

    }

    @DisplayName("JUnit test for createBooking method (negative scenario)")
    @Test
    public void givenUserIdWrong_whenCreateItem_thenThrowException() {

        given(itemRepository.findById(anyLong())).willReturn(Optional.of(item1));
        given(userRepository.findById(anyLong())).willThrow(IllegalArgumentException.class);

        assertThrows(IllegalArgumentException.class, () -> bookingService.createBooking(user1.getId(), requestDto1));

        verify(itemRepository, times(1))
                .findById(any());
        verify(userRepository, times(1))
                .findById(any());

        verify(bookingRepository, times(0))
                .save(any());
    }

    @DisplayName("JUnit test for createBooking method (negative scenario)")
    @Test
    public void givenOwnerIdAndBookerIdIsTheSame_whenCreateItem_thenThrowException() {

        given(itemRepository.findById(anyLong())).willReturn(Optional.of(item1));
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user1));

        assertThrows(IllegalArgumentException.class, () -> bookingService.createBooking(user1.getId(), requestDto1));

        verify(itemRepository, times(1))
                .findById(any());
        verify(userRepository, times(1))
                .findById(any());

        verify(bookingRepository, times(0))
                .save(any());
    }

    @DisplayName("JUnit test for setApproveToBooking method")
    @Test
    public void givenUserIdAndBookingIdAndApproved_setApproveToBooking_thenReturnBookingDtoWithApproved() {

        Booking savedBooking = new Booking(1L, Timestamp.valueOf("2022-11-12 10:09:00"),
                Timestamp.valueOf("2022-12-13 10:09:00"), item1, user2, BookingStatus.APPROVED);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user1));
        given(bookingRepository.findById(anyLong())).willReturn(Optional.of(booking1));
        given(itemRepository.findById(anyLong())).willReturn(Optional.of(item1));
        given(bookingRepository.save(booking1)).willReturn(savedBooking);
        responseDto1.setStatus(BookingStatus.APPROVED);
        given(bookingMapper.toDto(booking1)).willReturn(responseDto1);

        BookingDtoToResponse resp = bookingService.setApproveToBooking(user1.getId(), booking1.getId(), true);

        assertThat(resp.getStatus()).isEqualTo(BookingStatus.APPROVED);

        verify(userRepository, times(1))
                .findById(any());
        verify(bookingRepository, times(1))
                .findById(any());
        verify(itemRepository, times(1))
                .findById(any());
        verify(bookingRepository, times(1))
                .save(any());

    }

    @DisplayName("JUnit test for setApproveToBooking method (negative scenario)")
    @Test
    public void givenUserIdAndWrongBookingId_setApproveToBooking_thenThrowException() {
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user1));
        given(bookingRepository.findById(-1L)).willThrow(IllegalArgumentException.class);

        assertThrows(IllegalArgumentException.class, () -> bookingService.setApproveToBooking(
                user1.getId(), -1L, true));

        verify(userRepository, times(1))
                .findById(any());
        verify(bookingRepository, times(1))
                .findById(any());
        verify(itemRepository, times(0))
                .findById(any());
        verify(bookingRepository, times(0))
                .save(any());

    }

    @DisplayName("JUnit test for setApproveToBooking method (negative scenario)")
    @Test
    public void givenWrongUserId_setApproveToBooking_thenThrowException() {
        given(userRepository.findById(anyLong())).willThrow(IllegalArgumentException.class);

        assertThrows(IllegalArgumentException.class, () -> bookingService.setApproveToBooking(
                -1L, 1L, true));

        verify(userRepository, times(1))
                .findById(any());
        verify(bookingRepository, times(0))
                .findById(any());
        verify(itemRepository, times(0))
                .findById(any());
        verify(bookingRepository, times(0))
                .save(any());

    }

    @DisplayName("JUnit test for setApproveToBooking method (negative scenario)")
    @Test
    public void givenItemIdAndWrong_setApproveToBooking_thenThrowException() {
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user1));
        given(bookingRepository.findById(anyLong())).willReturn(Optional.of(booking1));
        given(itemRepository.findById(anyLong())).willThrow(IllegalArgumentException.class);

        assertThrows(IllegalArgumentException.class, () -> bookingService.setApproveToBooking(
                user1.getId(), 1L, true));

        verify(userRepository, times(1))
                .findById(any());
        verify(bookingRepository, times(1))
                .findById(any());
        verify(itemRepository, times(1))
                .findById(any());
        verify(bookingRepository, times(0))
                .save(any());

    }

    @DisplayName("JUnit test for setApproveToBooking method (negative scenario)")
    @Test
    public void givenOwnerNotEqualUser_setApproveToBooking_thenThrowException() {

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user2));
        given(bookingRepository.findById(anyLong())).willReturn(Optional.of(booking1));
        given(itemRepository.findById(anyLong())).willReturn(Optional.of(item1));

        assertThrows(IllegalArgumentException.class, () -> bookingService.setApproveToBooking(
                user2.getId(), booking1.getId(), true));

        verify(userRepository, times(1))
                .findById(any());
        verify(bookingRepository, times(1))
                .findById(any());
        verify(itemRepository, times(1))
                .findById(any());
        verify(bookingRepository, times(0))
                .save(any());

    }

    @DisplayName("JUnit test for setApproveToBooking method (negative scenario)")
    @Test
    public void givenBookingWithApprovedStatus_setApproveToBooking_thenThrowException() {
        booking1.setStatus(BookingStatus.APPROVED);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user1));
        given(bookingRepository.findById(anyLong())).willReturn(Optional.of(booking1));
        given(itemRepository.findById(anyLong())).willReturn(Optional.of(item1));

        assertThrows(ValidationException.class, () -> bookingService.setApproveToBooking(
                user2.getId(), booking1.getId(), true));

        verify(userRepository, times(1))
                .findById(any());
        verify(bookingRepository, times(1))
                .findById(any());
        verify(itemRepository, times(1))
                .findById(any());
        verify(bookingRepository, times(0))
                .save(any());

    }

    @DisplayName("JUnit test for setApproveToBooking method (negative scenario)")
    @Test
    public void givenBookingWithApprovedFalse_setApproveToBooking_thenThrowException() {

        Booking savedBooking = new Booking(1L, Timestamp.valueOf("2022-11-12 10:09:00"),
                Timestamp.valueOf("2022-12-13 10:09:00"), item1, user2, BookingStatus.REJECTED);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user1));
        given(bookingRepository.findById(anyLong())).willReturn(Optional.of(booking1));
        given(itemRepository.findById(anyLong())).willReturn(Optional.of(item1));
        given(bookingRepository.save(booking1)).willReturn(savedBooking);
        responseDto1.setStatus(BookingStatus.REJECTED);
        given(bookingMapper.toDto(booking1)).willReturn(responseDto1);

        BookingDtoToResponse resp = bookingService.setApproveToBooking(user1.getId(), booking1.getId(), false);

        assertThat(resp.getStatus()).isEqualTo(BookingStatus.REJECTED);

        verify(userRepository, times(1))
                .findById(any());
        verify(bookingRepository, times(1))
                .findById(any());
        verify(itemRepository, times(1))
                .findById(any());
        verify(bookingRepository, times(1))
                .save(any());

    }

    @DisplayName("JUnit test for getBooking method")
    @Test
    public void givenOwnerIdAhdBookingId_getBooking_thenReturnBookingDto() {
        given(bookingRepository.findById(anyLong())).willReturn(Optional.of(booking1));
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user1));
        given(itemRepository.findById(anyLong())).willReturn(Optional.of(item1));
        given(bookingMapper.toDto(booking1)).willReturn(responseDto1);

        BookingDtoToResponse resp = bookingService.getBooking(user1.getId(), item1.getId());

        assertThat(resp).isNotNull();
        assertThat(resp).isEqualTo(responseDto1);

        verify(userRepository, times(1))
                .findById(any());
        verify(bookingRepository, times(1))
                .findById(any());
        verify(itemRepository, times(1))
                .findById(any());

    }

    @DisplayName("JUnit test for getBooking method (negative scenario)")
    @Test
    public void givenUserIsNotOwnerOrBooker_getBooking_thenThrowException() {
        User user3 = new User(3L, "Alla", "alla@gmail.com");
        given(bookingRepository.findById(anyLong())).willReturn(Optional.of(booking1));
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user3));
        given(itemRepository.findById(anyLong())).willReturn(Optional.of(item1));

        assertThrows(IllegalArgumentException.class, () -> bookingService.getBooking(
                user3.getId(), booking1.getId()));

        verify(userRepository, times(1))
                .findById(any());
        verify(bookingRepository, times(1))
                .findById(any());
        verify(itemRepository, times(1))
                .findById(any());
    }

    @DisplayName("JUnit test for getBooking method (negative scenario)")
    @Test
    public void givenWrongItemId_getBooking_thenThrowException() {
        User user3 = new User(3L, "Alla", "alla@gmail.com");
        given(bookingRepository.findById(anyLong())).willReturn(Optional.of(booking1));
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user3));
        given(itemRepository.findById(anyLong())).willThrow(IllegalArgumentException.class);

        assertThrows(IllegalArgumentException.class, () -> bookingService.getBooking(
                user3.getId(), booking1.getId()));

        verify(userRepository, times(1))
                .findById(any());
        verify(bookingRepository, times(1))
                .findById(any());
        verify(itemRepository, times(1))
                .findById(any());
    }

    @DisplayName("JUnit test for getBooking method (negative scenario)")
    @Test
    public void givenWrongUserId_getBooking_thenThrowException() {
        User user3 = new User(3L, "Alla", "alla@gmail.com");
        given(bookingRepository.findById(anyLong())).willReturn(Optional.of(booking1));
        given(userRepository.findById(anyLong())).willThrow(IllegalArgumentException.class);

        assertThrows(IllegalArgumentException.class, () -> bookingService.getBooking(
                user3.getId(), booking1.getId()));

        verify(userRepository, times(1))
                .findById(any());
        verify(bookingRepository, times(1))
                .findById(any());
        verify(itemRepository, times(0))
                .findById(any());
    }

    @DisplayName("JUnit test for getBooking method (negative scenario)")
    @Test
    public void givenWrongBookingId_getBooking_thenThrowException() {
        User user3 = new User(3L, "Alla", "alla@gmail.com");
        given(bookingRepository.findById(anyLong())).willThrow(IllegalArgumentException.class);

        assertThrows(IllegalArgumentException.class, () -> bookingService.getBooking(
                user3.getId(), booking1.getId()));

        verify(bookingRepository, times(1))
                .findById(any());
        verify(userRepository, times(0))
                .findById(any());
        verify(itemRepository, times(0))
                .findById(any());
    }

    @DisplayName("JUnit test for getBookingsOfUserAllOrByState method")
    @Test
    public void givenUserIdAhdBookingStateALL_getBookingsOfUserAllOrByState_thenReturnListBookingDto() {
        int from = 0;
        int size = 2;

        Page<Booking> page = new PageImpl<>(List.of(booking1));

        given(userRepository.existsById(anyLong())).willReturn(true);
        given(bookingRepository.findAll(PageRequest.of(from,size, SORT))).willReturn(page);
        given(bookingMapper.toDto(booking1)).willReturn(responseDto1);

        List<BookingDtoToResponse> respList = bookingService.getBookingsOfUserAllOrByState(
                user1.getId(), BookingState.ALL, from, size);

        assertThat(respList.size()).isEqualTo(1);
        assertThat(respList.get(0)).isEqualTo(responseDto1);

        verify(bookingRepository, times(1))
                .findAll(PageRequest.of(from,size, SORT));
        verify(userRepository, times(1))
                .existsById(anyLong());

    }

    @DisplayName("JUnit test for getBookingsOfUserAllOrByState method")
    @Test
    public void givenUserIdAhdBookingStatePast_getBookingsOfUserAllOrByState_thenReturnListBookingDto() {
        int from = 0;
        int size = 2;

        Page<Booking> page = new PageImpl<>(List.of(booking1));

        given(userRepository.existsById(anyLong())).willReturn(true);
        given(bookingRepository.findAllByBookerIdAndEndBefore(
                any(), any(), any())).willReturn(page);
        given(bookingMapper.toDto(booking1)).willReturn(responseDto1);

        List<BookingDtoToResponse> respList = bookingService.getBookingsOfUserAllOrByState(
                user1.getId(), BookingState.PAST, from, size);

        assertThat(respList.size()).isEqualTo(1);
        assertThat(respList.get(0).getId()).isEqualTo(responseDto1.getId());

        verify(bookingRepository, times(1))
                .findAllByBookerIdAndEndBefore(any(), any(), any());

        verify(userRepository, times(1))
                .existsById(anyLong());

    }

    @DisplayName("JUnit test for getBookingsOfUserAllOrByState method")
    @Test
    public void givenUserIdAhdBookingStateCurrent_getBookingsOfUserAllOrByState_thenReturnListBookingDto() {
        int from = 0;
        int size = 2;

        Page<Booking> page = new PageImpl<>(List.of(booking1));

        given(userRepository.existsById(anyLong())).willReturn(true);
        given(bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(
                any(), any(), any(), any())).willReturn(page);
        given(bookingMapper.toDto(booking1)).willReturn(responseDto1);

        List<BookingDtoToResponse> respList = bookingService.getBookingsOfUserAllOrByState(
                user1.getId(), BookingState.CURRENT, from, size);

        assertThat(respList.size()).isEqualTo(1);
        assertThat(respList.get(0).getId()).isEqualTo(responseDto1.getId());

        verify(bookingRepository, times(1))
                .findAllByBookerIdAndStartBeforeAndEndAfter(any(), any(), any(), any());

        verify(userRepository, times(1))
                .existsById(anyLong());

    }

    @DisplayName("JUnit test for getBookingsOfUserAllOrByState method")
    @Test
    public void givenUserIdAhdBookingStateFuture_getBookingsOfUserAllOrByState_thenReturnListBookingDto() {
        int from = 0;
        int size = 2;

        Page<Booking> page = new PageImpl<>(List.of(booking1));

        given(userRepository.existsById(anyLong())).willReturn(true);
        given(bookingRepository.findAllByBookerIdAndStartAfter(
                any(), any(), any())).willReturn(page);
        given(bookingMapper.toDto(booking1)).willReturn(responseDto1);

        List<BookingDtoToResponse> respList = bookingService.getBookingsOfUserAllOrByState(
                user1.getId(), BookingState.FUTURE, from, size);

        assertThat(respList.size()).isEqualTo(1);
        assertThat(respList.get(0).getId()).isEqualTo(responseDto1.getId());

        verify(bookingRepository, times(1))
                .findAllByBookerIdAndStartAfter(any(), any(), any());

        verify(userRepository, times(1))
                .existsById(anyLong());

    }

    @DisplayName("JUnit test for getBookingsOfUserAllOrByState method")
    @Test
    public void givenUserIdAhdBookingStateWaiting_getBookingsOfUserAllOrByState_thenReturnListBookingDto() {
        int from = 0;
        int size = 2;

        Page<Booking> page = new PageImpl<>(List.of(booking1));

        given(userRepository.existsById(anyLong())).willReturn(true);
        given(bookingRepository.findAllByBookerIdAndStatus(
                any(), any(), any())).willReturn(page);
        given(bookingMapper.toDto(booking1)).willReturn(responseDto1);

        List<BookingDtoToResponse> respList = bookingService.getBookingsOfUserAllOrByState(
                user1.getId(), BookingState.WAITING, from, size);

        assertThat(respList.size()).isEqualTo(1);
        assertThat(respList.get(0).getId()).isEqualTo(responseDto1.getId());

        verify(bookingRepository, times(1))
                .findAllByBookerIdAndStatus(any(), any(), any());

        verify(userRepository, times(1))
                .existsById(anyLong());

    }

    @DisplayName("JUnit test for getBookingsOfUserAllOrByState method")
    @Test
    public void givenUserIdAhdBookingStateRejected_getBookingsOfUserAllOrByState_thenReturnListBookingDto() {
        int from = 0;
        int size = 2;

        Page<Booking> page = new PageImpl<>(List.of(booking1));

        given(userRepository.existsById(anyLong())).willReturn(true);
        given(bookingRepository.findAllByBookerIdAndStatus(
                any(), any(), any())).willReturn(page);
        given(bookingMapper.toDto(booking1)).willReturn(responseDto1);

        List<BookingDtoToResponse> respList = bookingService.getBookingsOfUserAllOrByState(
                user1.getId(), BookingState.REJECTED, from, size);

        assertThat(respList.size()).isEqualTo(1);
        assertThat(respList.get(0).getId()).isEqualTo(responseDto1.getId());

        verify(bookingRepository, times(1))
                .findAllByBookerIdAndStatus(any(), any(), any());

        verify(userRepository, times(1))
                .existsById(anyLong());

    }

    @DisplayName("JUnit test for getBookingsOfUserAllOrByState method (negative scenario)")
    @Test
    public void givenUserIdAhdBookingStateUnsupported_getBookingsOfUserAllOrByState_thenThrowException() {
        int from = 0;
        int size = 2;

        given(userRepository.existsById(anyLong())).willReturn(true);

        assertThrows(UnknownStateException.class, () -> bookingService.getBookingsOfUserAllOrByState(
                user1.getId(), BookingState.UNSUPPORTED_STATUS, from, size));

        verify(userRepository, times(1))
                .existsById(anyLong());

        verify(bookingRepository, times(0))
                .findAll(PageRequest.of(from,size, SORT));
        verify(bookingRepository, times(0))
                .findAllByBookerIdAndEndBefore(any(), any(), any());
        verify(bookingRepository, times(0))
                .findAllByBookerIdAndStartBeforeAndEndAfter(any(), any(), any(), any());
        verify(bookingRepository, times(0))
                .findAllByBookerIdAndStartAfter(any(), any(), any());
        verify(bookingRepository, times(0))
                .findAllByBookerIdAndStatus(any(), any(), any());

    }

    @DisplayName("JUnit test for getBookingsOfUserAllOrByState method (negative scenario)")
    @Test
    public void givenWrongSize_getBookingsOfUserAllOrByState_thenThrowException() {
        int from = 0;
        int size = 0;

        assertThrows(ValidationException.class, () -> bookingService.getBookingsOfUserAllOrByState(
                user1.getId(), BookingState.ALL, from, size));


        verify(bookingRepository, times(0))
                .findAll(PageRequest.of(1,1, SORT));
        verify(userRepository, times(0))
                .existsById(anyLong());

    }

    @DisplayName("JUnit test for getBookingsOfUserAllOrByState method (negative scenario)")
    @Test
    public void givenWrongFrom_getBookingsOfUserAllOrByState_thenThrowException() {
        int from = -2;
        int size = 5;

        assertThrows(ValidationException.class, () -> bookingService.getBookingsOfUserAllOrByState(
                user1.getId(), BookingState.ALL, from, size));


        verify(bookingRepository, times(0))
                .findAll(PageRequest.of(1,1, SORT));
        verify(userRepository, times(0))
                .existsById(anyLong());

    }

    @DisplayName("JUnit test for getBookingsOfUserAllOrByState method (negative scenario)")
    @Test
    public void givenUserWrongId_getBookingsOfUserAllOrByState_thenThrowException() {
        int from = 0;
        int size = 2;

        given(userRepository.existsById(anyLong())).willThrow(IllegalArgumentException.class);

        assertThrows(IllegalArgumentException.class, () -> bookingService.getBookingsOfUserAllOrByState(
                user1.getId(), BookingState.ALL, from, size));

        verify(bookingRepository, times(0))
                .findAll(PageRequest.of(from,size, SORT));
        verify(userRepository, times(1))
                .existsById(anyLong());

    }

    @DisplayName("JUnit test for getBookingsAllOrByStateForEveryUserItem method")
    @Test
    public void givenUserIdAhdBookingStateFromSize_getBookingsAllOrByStateForEveryUserItem_thenReturnListBookingDto() {
        int from = 0;
        int size = 2;

        User user3 = new User(3L, "Alla", "alla@gmail.com");

        Item item2 = new Item(2L, "Аккумулятор", "Аккумулятор для машины", true);
        item2.setOwner(user3);
        Item item3 = new Item(3L, "Дрель", "Дрель для всего", true);
        item3.setOwner(user3);

        Booking booking2 = new Booking(2L, Timestamp.valueOf("2023-01-17 10:09:00"),
                Timestamp.valueOf("2023-01-30 10:09:00"), item2, user1, BookingStatus.APPROVED);

        Booking booking3 = new Booking(3L, Timestamp.valueOf("2023-02-17 10:09:00"),
                Timestamp.valueOf("2023-03-30 10:09:00"), item3, user2, BookingStatus.APPROVED);

        Page<Booking> page = new PageImpl<>(List.of(booking2, booking3));

        BookingDtoFromRequest requestDto2 = new BookingDtoFromRequest();
        requestDto2.setItemId(2L);
        requestDto2.setStart(LocalDateTime.parse("2023-01-17T10:09:00"));
        requestDto2.setEnd(LocalDateTime.parse("2023-01-30T10:09:00"));

        BookingDtoToResponse responseDto2 = new BookingDtoToResponse(
                2L,
                LocalDateTime.parse("2023-01-17T10:09:00"),
                LocalDateTime.parse("2023-01-30T10:09:00"),
                BookingStatus.APPROVED,
                UserDtoBookingToResponse.builder().id(1L).build(),
                ItemDtoBookingToResponse.builder().id(2L).name("Аккумулятор").build()

        );

        BookingDtoFromRequest requestDto3 = new BookingDtoFromRequest();
        requestDto3.setItemId(3L);
        requestDto3.setStart(LocalDateTime.parse("2023-02-17T10:09:00"));
        requestDto3.setEnd(LocalDateTime.parse("2023-03-30T10:09:00"));

        BookingDtoToResponse responseDto3 = new BookingDtoToResponse(
                3L,
                LocalDateTime.parse("2023-02-17T10:09:00"),
                LocalDateTime.parse("2023-03-30T10:09:00"),
                BookingStatus.APPROVED,
                UserDtoBookingToResponse.builder().id(2L).build(),
                ItemDtoBookingToResponse.builder().id(3L).name("Дрель").build());

        given(userRepository.existsById(anyLong())).willReturn(true);
        given(bookingRepository.findAllByItemOwnerIdAndStartAfter(
                anyLong(), any(Timestamp.class), any(PageRequest.class))).willReturn(page);
        given(bookingMapper.toDto(booking2)).willReturn(responseDto2);
        given(bookingMapper.toDto(booking3)).willReturn(responseDto3);

        List<BookingDtoToResponse> respList = bookingService.getBookingsAllOrByStateForEveryUserItem(
                user3.getId(), BookingState.FUTURE, from, size);

        assertThat(respList.size()).isEqualTo(2);

        verify(userRepository, times(1))
                .existsById(anyLong());
        verify(bookingRepository, times(1))
                .findAllByItemOwnerIdAndStartAfter(anyLong(), any(Timestamp.class), any(PageRequest.class));

    }

    @DisplayName("JUnit test for getBookingsAllOrByStateForEveryUserItem method (negative scenario)")
    @Test
    public void givenUserNotExist_getBookingsAllOrByStateForEveryUserItem_thenThrowException() {
        int from = 0;
        int size = 2;

        given(userRepository.existsById(anyLong())).willThrow(IllegalArgumentException.class);

        assertThrows(IllegalArgumentException.class, () -> bookingService.getBookingsAllOrByStateForEveryUserItem(
                user1.getId(), BookingState.ALL, from, size));

        verify(bookingRepository, times(0))
                .findAll(PageRequest.of(from,size, SORT));
        verify(userRepository, times(1))
                .existsById(anyLong());

    }

    @DisplayName("JUnit test for getBookingsAllOrByStateForEveryUserItem method (negative scenario)")
    @Test
    public void givenWrongFrom_getBookingsAllOrByStateForEveryUserItem_thenThrowException() {
        int from = -2;
        int size = 5;

        assertThrows(ValidationException.class, () -> bookingService.getBookingsAllOrByStateForEveryUserItem(
                user1.getId(), BookingState.ALL, from, size));


        verify(bookingRepository, times(0))
                .findAll(PageRequest.of(1,1, SORT));
        verify(userRepository, times(0))
                .existsById(anyLong());

    }

    @DisplayName("JUnit test for getBookingsAllOrByStateForEveryUserItem method (negative scenario)")
    @Test
    public void givenWrongSize_getBookingsAllOrByStateForEveryUserItem_thenThrowException() {
        int from = 1;
        int size = 0;

        assertThrows(ValidationException.class, () -> bookingService.getBookingsAllOrByStateForEveryUserItem(
                user1.getId(), BookingState.ALL, from, size));


        verify(bookingRepository, times(0))
                .findAll(PageRequest.of(1,1, SORT));
        verify(userRepository, times(0))
                .existsById(anyLong());

    }

    @DisplayName("JUnit test for getBookingsOfUserAllOrByState method")
    @Test
    public void givenStateAll_getBookingsAllOrByStateForEveryUserItem_thenReturnListBookingDto() {
        int from = 0;
        int size = 2;

        Page<Booking> page = new PageImpl<>(List.of(booking1));

        given(userRepository.existsById(anyLong())).willReturn(true);
        given(bookingRepository.findAllByItemOwnerId(
                any(), any())).willReturn(page);
        given(bookingMapper.toDto(booking1)).willReturn(responseDto1);

        List<BookingDtoToResponse> respList = bookingService.getBookingsAllOrByStateForEveryUserItem(
                user1.getId(), BookingState.ALL, from, size);

        assertThat(respList.size()).isEqualTo(1);
        assertThat(respList.get(0).getId()).isEqualTo(responseDto1.getId());

        verify(bookingRepository, times(1))
                .findAllByItemOwnerId(any(), any());

        verify(userRepository, times(1))
                .existsById(anyLong());

    }

    @DisplayName("JUnit test for getBookingsOfUserAllOrByState method")
    @Test
    public void givenStatePast_getBookingsAllOrByStateForEveryUserItem_thenReturnListBookingDto() {
        int from = 0;
        int size = 2;

        Page<Booking> page = new PageImpl<>(List.of(booking1));

        given(userRepository.existsById(anyLong())).willReturn(true);
        given(bookingRepository.findAllByItemOwnerIdAndEndBefore(
                any(), any(), any())).willReturn(page);
        given(bookingMapper.toDto(booking1)).willReturn(responseDto1);

        List<BookingDtoToResponse> respList = bookingService.getBookingsAllOrByStateForEveryUserItem(
                user1.getId(), BookingState.PAST, from, size);

        assertThat(respList.size()).isEqualTo(1);
        assertThat(respList.get(0).getId()).isEqualTo(responseDto1.getId());

        verify(bookingRepository, times(1))
                .findAllByItemOwnerIdAndEndBefore(any(), any(), any());

        verify(userRepository, times(1))
                .existsById(anyLong());

    }

    @DisplayName("JUnit test for getBookingsOfUserAllOrByState method")
    @Test
    public void givenStateCurrent_getBookingsAllOrByStateForEveryUserItem_thenReturnListBookingDto() {
        int from = 0;
        int size = 2;

        Page<Booking> page = new PageImpl<>(List.of(booking1));

        given(userRepository.existsById(anyLong())).willReturn(true);
        given(bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfter(any(),
                any(), any(), any())).willReturn(page);
        given(bookingMapper.toDto(booking1)).willReturn(responseDto1);

        List<BookingDtoToResponse> respList = bookingService.getBookingsAllOrByStateForEveryUserItem(
                user1.getId(), BookingState.CURRENT, from, size);

        assertThat(respList.size()).isEqualTo(1);
        assertThat(respList.get(0).getId()).isEqualTo(responseDto1.getId());

        verify(bookingRepository, times(1))
                .findAllByItemOwnerIdAndStartBeforeAndEndAfter(any(), any(), any(), any());

        verify(userRepository, times(1))
                .existsById(anyLong());

    }

    @DisplayName("JUnit test for getBookingsOfUserAllOrByState method")
    @Test
    public void givenStateFuture_getBookingsAllOrByStateForEveryUserItem_thenReturnListBookingDto() {
        int from = 0;
        int size = 2;

        Page<Booking> page = new PageImpl<>(List.of(booking1));

        given(userRepository.existsById(anyLong())).willReturn(true);
        given(bookingRepository.findAllByItemOwnerIdAndStartAfter(any(),
                any(), any())).willReturn(page);
        given(bookingMapper.toDto(booking1)).willReturn(responseDto1);

        List<BookingDtoToResponse> respList = bookingService.getBookingsAllOrByStateForEveryUserItem(
                user1.getId(), BookingState.FUTURE, from, size);

        assertThat(respList.size()).isEqualTo(1);
        assertThat(respList.get(0).getId()).isEqualTo(responseDto1.getId());

        verify(bookingRepository, times(1))
                .findAllByItemOwnerIdAndStartAfter(any(), any(), any());

        verify(userRepository, times(1))
                .existsById(anyLong());

    }

    @DisplayName("JUnit test for getBookingsOfUserAllOrByState method")
    @Test
    public void givenStateWaiting_getBookingsAllOrByStateForEveryUserItem_thenReturnListBookingDto() {
        int from = 0;
        int size = 2;

        Page<Booking> page = new PageImpl<>(List.of(booking1));

        given(userRepository.existsById(anyLong())).willReturn(true);
        given(bookingRepository.findAllByItemOwnerIdAndStatus(any(),
                any(), any())).willReturn(page);
        given(bookingMapper.toDto(booking1)).willReturn(responseDto1);

        List<BookingDtoToResponse> respList = bookingService.getBookingsAllOrByStateForEveryUserItem(
                user1.getId(), BookingState.WAITING, from, size);

        assertThat(respList.size()).isEqualTo(1);
        assertThat(respList.get(0).getId()).isEqualTo(responseDto1.getId());

        verify(bookingRepository, times(1))
                .findAllByItemOwnerIdAndStatus(any(), any(), any());

        verify(userRepository, times(1))
                .existsById(anyLong());

    }

    @DisplayName("JUnit test for getBookingsOfUserAllOrByState method")
    @Test
    public void givenStateRejected_getBookingsAllOrByStateForEveryUserItem_thenReturnListBookingDto() {
        int from = 0;
        int size = 2;

        Page<Booking> page = new PageImpl<>(List.of(booking1));

        given(userRepository.existsById(anyLong())).willReturn(true);
        given(bookingRepository.findAllByItemOwnerIdAndStatus(any(),
                any(), any())).willReturn(page);
        given(bookingMapper.toDto(booking1)).willReturn(responseDto1);

        List<BookingDtoToResponse> respList = bookingService.getBookingsAllOrByStateForEveryUserItem(
                user1.getId(), BookingState.REJECTED, from, size);

        assertThat(respList.size()).isEqualTo(1);
        assertThat(respList.get(0).getId()).isEqualTo(responseDto1.getId());

        verify(bookingRepository, times(1))
                .findAllByItemOwnerIdAndStatus(any(), any(), any());

        verify(userRepository, times(1))
                .existsById(anyLong());

    }

    @DisplayName("JUnit test for getBookingsOfUserAllOrByState method")
    @Test
    public void givenStateUnsupported_getBookingsAllOrByStateForEveryUserItem_thenReturnListBookingDto() {
        int from = 0;
        int size = 2;

        given(userRepository.existsById(anyLong())).willReturn(true);

        assertThrows(UnknownStateException.class, () -> bookingService.getBookingsAllOrByStateForEveryUserItem(
               user1.getId(), BookingState.UNSUPPORTED_STATUS, from, size));

        verify(userRepository, times(1))
                .existsById(anyLong());

        verify(bookingRepository, times(0))
                .findAllByItemOwnerId(any(), any());
        verify(bookingRepository, times(0))
                .findAllByItemOwnerIdAndEndBefore(any(), any(), any());
        verify(bookingRepository, times(0))
                .findAllByItemOwnerIdAndStartBeforeAndEndAfter(any(), any(), any(), any());
        verify(bookingRepository, times(0))
                .findAllByItemOwnerIdAndStartAfter(any(), any(), any());
        verify(bookingRepository, times(0))
                .findAllByItemOwnerIdAndStatus(any(), any(), any());

    }

}
