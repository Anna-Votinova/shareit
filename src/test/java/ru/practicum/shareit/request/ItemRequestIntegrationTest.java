package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
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
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestIntegrationTest {

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    private final ItemRequestController itemRequestController;
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @DisplayName("Integration test for findAllPageable method")
    @Test
    public void givenUserIdAhdFromAndSize_whenFindAllPageable_thenReturnListBookingDto() {

        int from = 0;
        int size = 5;

        User user1 = new User(null, "Anna", "anna13@36on.ru");
        User user2 = new User(null, "Olga", "olga@gmail.com");
        user1 = userRepository.save(user1);
        user2 = userRepository.save(user2);

        ItemRequest request = ItemRequest.builder()
                .id(1L)
                .description("Хотел бы взять в аренду чесалку для кота")
                .requester(user2)
                .created(Timestamp.valueOf(LocalDateTime.parse("2022-12-18T20:00:57")))
                .build();

        ItemRequest newReq = ItemRequest.builder()
                .id(2L)
                .description("Нужна лопата")
                .created(Timestamp.valueOf(LocalDateTime.parse("2022-12-20T20:00:57")))
                .requester(user2)
                .build();

        itemRequestRepository.save(request);
        newReq = itemRequestRepository.save(newReq);

        Item item1 = new Item(null, "Лопата", "Удобно копать картоху", true);
        item1.setOwner(user1);
        item1.setRequest(newReq);

        itemRepository.save(item1);


        List<ItemRequestDto> itemRequestDtoList = itemRequestController.findAllPageable(user1.getId(), from, size);

        assertThat(itemRequestDtoList.size()).isEqualTo(2);
        assertThat(itemRequestDtoList.get(0).getId()).isEqualTo(2);
        assertThat(itemRequestDtoList.get(0).getItems().get(0).getName()).isEqualTo("Лопата");
    }
}
