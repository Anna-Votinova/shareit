package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoToResponse;
import ru.practicum.shareit.booking.dto.ItemDtoBookingToResponse;
import ru.practicum.shareit.booking.dto.UserDtoBookingToResponse;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingIntegrationTest {

    private final BookingController bookingController;

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @DisplayName("Integration test for getBookingsAllOrByStateForEveryUserItem method")
    @Test
    public void givenUserIdAhdBookingStateFromSize_getBookingsAllOrByStateForEveryUserItem_thenReturnListBookingDto() {


        int from = 0;
        int size = 5;

        User user1 = new User(null, "Anna", "anna13@36on.ru");
        User user2 = new User(null, "Olga", "olga@gmail.com");
        User user3 = new User(null, "Alla", "alla@gmail.com");

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

        Booking booking2 = new Booking(null, Timestamp.valueOf("2023-01-17 10:09:00"),
                Timestamp.valueOf("2023-01-30 10:09:00"), item2, user1, BookingStatus.APPROVED);

        BookingDtoToResponse responseDto2 = new BookingDtoToResponse(
                2L,
                LocalDateTime.parse("2023-01-17T10:09:00"),
                LocalDateTime.parse("2023-01-30T10:09:00"),
                BookingStatus.APPROVED,
                UserDtoBookingToResponse.builder().id(1L).build(),
                ItemDtoBookingToResponse.builder().id(2L).name("Аккумулятор").build()

        );

        Booking booking3 = new Booking(null, Timestamp.valueOf("2023-02-17 10:09:00"),
                Timestamp.valueOf("2023-03-30 10:09:00"), item3, user2, BookingStatus.APPROVED);

        BookingDtoToResponse responseDto3 = new BookingDtoToResponse(
                3L,
                LocalDateTime.parse("2023-02-17T10:09:00"),
                LocalDateTime.parse("2023-03-30T10:09:00"),
                BookingStatus.APPROVED,
                UserDtoBookingToResponse.builder().id(2L).build(),
                ItemDtoBookingToResponse.builder().id(3L).name("Дрель").build());

        bookingRepository.save(booking1);
        bookingRepository.save(booking2);
        bookingRepository.save(booking3);



        List<BookingDtoToResponse> respList = bookingController.getBookingsAllOrByStateForOwner(
                user3.getId(), BookingState.FUTURE, from, size);

        assertThat(respList.size()).isEqualTo(2);
        assertThat(respList.get(0)).isEqualTo(responseDto3);
        assertThat(respList.get(1)).isEqualTo(responseDto2);




    }

}
