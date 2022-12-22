package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemIntegrationTest {

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    private final BookingRepository bookingRepository;

    private final CommentRepository commentRepository;

    private final ItemController itemController;

    @DisplayName("Integration test for findAllPageable method")
    @Test
    public void givenUsersItemsBookingsComment_whenGetAllItemDto_thenListOfItemDto() {

        User user1 = new User(null, "Anna", "anna13@36on.ru");
        User user2 = new User(null, "Olga", "olga@gmail.com");
        User user3 = new User(null, "Alla", "alla@gmail.com");

        user1 = userRepository.save(user1);
        user2 = userRepository.save(user2);
        user3 = userRepository.save(user3);

        Item item1 = new Item(null, "Вещь", "Хорошая вещь", true);
        item1.setOwner(user1);
        Item item2 = new Item(null, "Аккумулятор", "Аккумулятор для машины", true);
        item2.setOwner(user1);
        Item item3 = new Item(null, "Дрель", "Дрель для всего", true);
        item3.setOwner(user1);

        item1 = itemRepository.save(item1);
        item2 = itemRepository.save(item2);
        item3 = itemRepository.save(item3);

        Booking booking1 = new Booking(null, Timestamp.valueOf("2022-11-12 10:09:00"),
                Timestamp.valueOf("2022-12-13 10:09:00"), item1, user2, BookingStatus.APPROVED);
        Booking booking2 = new Booking(null, Timestamp.valueOf("2022-12-17 10:09:00"),
                Timestamp.valueOf("2023-09-31 10:09:00"), item1, user3, BookingStatus.APPROVED);

        bookingRepository.save(booking1);
        booking2 = bookingRepository.save(booking2);

        Comment comment = new Comment(null, "Действительно хорошая вещь");
        comment.setItem(item1);
        comment.setAuthor(user2);
        comment.setCreated(Timestamp.valueOf(LocalDateTime.now()));

        commentRepository.save(comment);

        List<ItemDto> grandAnswerList = itemController.getAllItemsOfUser(user1.getId(), 0, 5);


        assertThat(grandAnswerList.size(), equalTo(3));
        assertThat(grandAnswerList.get(0).getComments().size(), equalTo(1));
        assertThat(grandAnswerList.get(0).getId(), notNullValue());
        assertThat(grandAnswerList.get(0).getName(), equalTo(item1.getName()));
        assertThat(grandAnswerList.get(0).getDescription(), equalTo(item1.getDescription()));
        assertThat(grandAnswerList.get(0).getAvailable(), equalTo(item1.getAvailable()));
        assertThat(grandAnswerList.get(0).getLastBooking().getBookerId(), equalTo(booking2.getBooker().getId()));
        assertThat(grandAnswerList.get(0).getNextBooking(), equalTo(null));
        assertThat(grandAnswerList.get(1).getId(), notNullValue());
        assertThat(grandAnswerList.get(1).getName(), equalTo(item2.getName()));
        assertThat(grandAnswerList.get(1).getDescription(), equalTo(item2.getDescription()));
        assertThat(grandAnswerList.get(1).getAvailable(), equalTo(item2.getAvailable()));
        assertThat(grandAnswerList.get(1).getComments(), equalTo(Collections.emptySet()));//возможно,убрать
        assertThat(grandAnswerList.get(2).getId(), notNullValue());
        assertThat(grandAnswerList.get(2).getName(), equalTo(item3.getName()));
        assertThat(grandAnswerList.get(2).getDescription(), equalTo(item3.getDescription()));
        assertThat(grandAnswerList.get(2).getAvailable(), equalTo(item3.getAvailable()));
        assertThat(grandAnswerList.get(2).getComments(), equalTo(Collections.emptySet()));


    }
}
