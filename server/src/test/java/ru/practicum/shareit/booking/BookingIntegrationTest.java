package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoToResponse;
import ru.practicum.shareit.booking.dto.ItemDtoBookingToResponse;
import ru.practicum.shareit.booking.dto.UserDtoBookingToResponse;
import ru.practicum.shareit.exceptions.UnknownStateException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingIntegrationTest {

    private final BookingController bookingController;

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    private int from;

    private int size;

    private User user3;

    private BookingDtoToResponse responseDto1;

    private BookingDtoToResponse responseDto2;

    private BookingDtoToResponse responseDto3;

    private Booking booking3;


    @BeforeEach
    public void setUp() {


        from = 0;
        size = 5;

        User user1 = new User(null, "Anna", "anna13@36on.ru");
        User user2 = new User(null, "Olga", "olga@gmail.com");
        user3 = new User(null, "Alla", "alla@gmail.com");

        user1 = userRepository.save(user1);
        user2 = userRepository.save(user2);
        user3 = userRepository.save(user3);


        Item item1 = new Item(null, "Вещь", "Хорошая вещь", true);
        item1.setOwner(user3);
        Item item2 = new Item(null, "Аккумулятор", "Аккумулятор для машины", true);
        item2.setOwner(user3);
        Item item3 = new Item(null, "Дрель", "Дрель для всего", true);
        item3.setOwner(user3);

        item1 = itemRepository.save(item1);
        item2 = itemRepository.save(item2);
        item3 = itemRepository.save(item3);

        Booking booking1 = new Booking(null, Timestamp.valueOf("2022-11-12 10:09:00"),
                Timestamp.valueOf("2022-12-13 10:09:00"), item1, user2, BookingStatus.APPROVED);

        responseDto1 = new BookingDtoToResponse(
                1L,
                LocalDateTime.parse("2022-11-12T10:09:00"),
                LocalDateTime.parse("2022-12-13T10:09:00"),
                BookingStatus.APPROVED,
                UserDtoBookingToResponse.builder().id(2L).build(),
                ItemDtoBookingToResponse.builder().id(1L).name("Вещь").build());

        Booking booking2 = new Booking(null, Timestamp.valueOf("2023-01-17 10:09:00"),
                Timestamp.valueOf("2023-01-30 10:09:00"), item2, user1, BookingStatus.APPROVED);

        responseDto2 = new BookingDtoToResponse(
                2L,
                LocalDateTime.parse("2023-01-17T10:09:00"),
                LocalDateTime.parse("2023-01-30T10:09:00"),
                BookingStatus.APPROVED,
                UserDtoBookingToResponse.builder().id(1L).build(),
                ItemDtoBookingToResponse.builder().id(2L).name("Аккумулятор").build()

        );

        booking3 = new Booking(null, Timestamp.valueOf("2023-02-17 10:09:00"),
                Timestamp.valueOf("2023-03-30 10:09:00"), item3, user2, BookingStatus.APPROVED);

        responseDto3 = new BookingDtoToResponse(
                3L,
                LocalDateTime.parse("2023-02-17T10:09:00"),
                LocalDateTime.parse("2023-03-30T10:09:00"),
                BookingStatus.APPROVED,
                UserDtoBookingToResponse.builder().id(2L).build(),
                ItemDtoBookingToResponse.builder().id(3L).name("Дрель").build());

        bookingRepository.save(booking1);
        bookingRepository.save(booking2);
        bookingRepository.save(booking3);

    }

    @DisplayName("Integration test for getBookingsAllOrByStateForOwner method")
    @Test
    public void givenUserIdAhdBookingStateFromSize_whenGetBookingsAllOrByStateForOwner_thenReturnListBookingDto() {

        List<BookingDtoToResponse> respList = bookingController.getBookingsAllOrByStateForOwner(
                user3.getId(), BookingState.FUTURE, from, size);

        assertThat(respList.size()).isEqualTo(2);
        assertThat(respList.get(0)).isEqualTo(responseDto3);
        assertThat(respList.get(1)).isEqualTo(responseDto2);


    }

    @DisplayName("Integration test for getBookingsAllOrByStateForOwner method")
    @Test
    public void givenAllState_whenGetBookingsAllOrByStateForEveryUserItem_thenReturnListBookingDto() {

        List<BookingDtoToResponse> respList = bookingController.getBookingsAllOrByStateForOwner(
                user3.getId(), BookingState.ALL, from, size);

        assertThat(respList.size()).isEqualTo(3);
        assertThat(respList.get(0)).isEqualTo(responseDto3);
        assertThat(respList.get(1)).isEqualTo(responseDto2);
        assertThat(respList.get(2)).isEqualTo(responseDto1);

    }

    @DisplayName("Integration test for getBookingsAllOrByStateForOwner method")
    @Test
    public void givenBookingPastState_whenGetBookingsAllOrByStateForOwner_thenReturnListBookingDto() {

        List<BookingDtoToResponse> respList = bookingController.getBookingsAllOrByStateForOwner(
                user3.getId(), BookingState.PAST, from, size);

        assertThat(respList.size()).isEqualTo(1);
        assertThat(respList.get(0)).isEqualTo(responseDto1);

    }

    @DisplayName("Integration test for getBookingsAllOrByStateForOwner method")
    @Test
    public void givenBookingCurrentState_whenGetBookingsAllOrByStateForOwner_thenReturnEmptyListBookingDto() {

        List<BookingDtoToResponse> respList = bookingController.getBookingsAllOrByStateForOwner(
                user3.getId(), BookingState.CURRENT, from, size);

        assertThat(respList.size()).isEqualTo(0);

    }

    @DisplayName("Integration test for getBookingsAllOrByStateForOwner method")
    @Test
    public void givenBookingWaitingState_whenGetBookingsAllOrByStateForOwner_thenReturnListBookingDto() {

        booking3.setStatus(BookingStatus.WAITING);
        bookingRepository.save(booking3);


        List<BookingDtoToResponse> respList = bookingController.getBookingsAllOrByStateForOwner(
                user3.getId(), BookingState.WAITING, from, size);

        assertThat(respList.size()).isEqualTo(1);
        assertThat(respList.get(0)).isEqualTo(responseDto3);

    }

    @DisplayName("Integration test for getBookingsAllOrByStateForOwner method")
    @Test
    public void givenBookingRejectedState_whenGetBookingsAllOrByStateForOwner_thenReturnListBookingDto() {

        booking3.setStatus(BookingStatus.REJECTED);
        bookingRepository.save(booking3);


        List<BookingDtoToResponse> respList = bookingController.getBookingsAllOrByStateForOwner(
                user3.getId(), BookingState.REJECTED, from, size);

        assertThat(respList.size()).isEqualTo(1);
        assertThat(respList.get(0)).isEqualTo(responseDto3);

    }

    @DisplayName("Integration test for getBookingsAllOrByStateForOwner method")
    @Test
    public void givenFrom1AndSize2_whenGetBookingsAllOrByStateForEveryUserItem_thenReturnListBookingDto() {

        from = 1;
        size = 2;

        List<BookingDtoToResponse> respList = bookingController.getBookingsAllOrByStateForOwner(
                user3.getId(), BookingState.ALL, from, size);

        assertThat(respList.size()).isEqualTo(1);
        assertThat(respList.get(0)).isEqualTo(responseDto1);

    }

    @DisplayName("Integration test for getBookingsAllOrByStateForOwner method")
    @Test
    public void givenFrom2AndSize2_whenGetBookingsAllOrByStateForEveryUserItem_thenReturnListBookingDto() {

        from = 2;
        size = 2;

        List<BookingDtoToResponse> respList = bookingController.getBookingsAllOrByStateForOwner(
                user3.getId(), BookingState.ALL, from, size);

        assertThat(respList.size()).isEqualTo(0);

    }

    @DisplayName("Integration test for getBookingsAllOrByStateForOwner method (negative scenario)")
    @Test
    public void givenBookingUnsupportedState_whenGetBookingsAllOrByStateForOwner_thenThrowException() {

        assertThrows(UnknownStateException.class,
                () -> bookingController.getBookingsAllOrByStateForOwner(
                        user3.getId(), BookingState.UNSUPPORTED_STATUS, from, size));

    }

    @DisplayName("Integration test for getBookingsAllOrByStateForOwner method (negative scenario)")
    @Test
    public void givenIncorrectUserid_whenGetBookingsAllOrByStateForOwner_thenThrowException() {

        assertThrows(IllegalArgumentException.class,
                () -> bookingController.getBookingsAllOrByStateForOwner(
                        5L, BookingState.FUTURE, from, size));

    }

}
