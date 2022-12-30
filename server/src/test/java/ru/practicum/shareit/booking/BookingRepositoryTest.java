package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    void findAllByItemIdAndBookerIdAndEndBefore() {
        Item item = new Item();
        item.setAvailable(true);
        item.setName("some item");
        item.setDescription("some description");
        itemRepository.save(item);

        User user = new User();
        user.setName("Anton");
        user.setEmail("some@mail.ru");
        userRepository.save(user);

        Booking booking = new Booking();
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(BookingStatus.APPROVED);
        booking.setStart(Timestamp.valueOf(LocalDateTime.of(2022, 12, 12, 12, 30)));
        booking.setEnd(Timestamp.valueOf(LocalDateTime.of(2022, 12, 12, 12, 40)));
        bookingRepository.save(booking);

        Booking booking1 = new Booking();
        booking1.setBooker(user);
        booking1.setItem(item);
        booking1.setStatus(BookingStatus.WAITING);
        booking1.setStart(Timestamp.valueOf(LocalDateTime.of(2022, 12, 12, 12, 30)));
        booking1.setEnd(Timestamp.valueOf(LocalDateTime.of(2022, 12, 12, 12, 40)));
        bookingRepository.save(booking1);

        List<Booking> bookingList = bookingRepository.findAllByItemIdAndBookerIdAndEndBeforeAndStatus(
                item.getId(), user.getId(), Timestamp.valueOf(LocalDateTime.now()), BookingStatus.APPROVED);
        assertEquals(1, bookingList.size());
    }
}