package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.validation.ValidationException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoMoreInteractions;


@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private ItemMapper mapper;


    @InjectMocks
    private ItemServiceImpl itemService;

    private Item item;

    private ItemDto itemDto;

    private User user;

    @BeforeEach
    public void setUp() {

        itemDto = ItemDto.builder()
                .id(1L)
                .name("Щетка для кота")
                .description("Щетка для всех пород котов")
                .available(true)
                .build();

        item = new Item(
                1L,
                "Щетка для кота",
                "Щетка для всех пород котов",
                true
        );

        user = new User(
                1L,
                "Anna",
                "anna13@36on.ru"
        );

    }

    @AfterEach
    public void verifyInteractions() {
        verifyNoMoreInteractions(
                userRepository,
                itemRepository,
                bookingRepository,
                commentRepository,
                itemRequestRepository
        );
    }

    @DisplayName("JUnit test for create method")
    @Test
    public void givenItemDto_whenCreateItem_thenReturnItemDto() {


        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));

        given(mapper.fromDto(itemDto)).willReturn(item);

        item.setOwner(user);

        given(itemRepository.save(item)).willReturn(item);

        given(mapper.fromItem(item)).willReturn(itemDto);

        ItemDto savedDto = itemService.create(user.getId(), itemDto);

        assertThat(savedDto).isNotNull();
        assertThat(savedDto).isEqualTo(itemDto);

    }

    @DisplayName("JUnit test for create method")
    @Test
    public void givenItemDtoWithRequestId_whenCreateItem_thenReturnItemObjectWithRequestId() {

        itemDto.setRequestId(1L);

        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));

        given(mapper.fromDto(itemDto)).willReturn(item);

        item.setOwner(user);

        ItemRequest iReq = ItemRequest.builder()
                .id(1L)
                .description("Универсальная щетка для кота")
                .created(Timestamp.valueOf("2022-11-17 18:00:06"))
                .requester(new User(2L, "Olga", "olga@gmail.com"))
                .build();

        given(itemRequestRepository.findById(itemDto.getRequestId())).willReturn(Optional.of(iReq));

        item.setRequest(iReq);

        given(itemRepository.save(item)).willReturn(item);

        itemDto.setRequestId(iReq.getId());

        given(mapper.fromItem(item)).willReturn(itemDto);

        ItemDto savedDto = itemService.create(user.getId(), itemDto);

        assertThat(savedDto).isNotNull();
        assertThat(savedDto).isEqualTo(itemDto);
        assertThat(savedDto.getRequestId()).isEqualTo(iReq.getId());

    }

    @DisplayName("JUnit test for create method (negative scenario)")
    @Test
    public void givenItemDtoWithEmptyAvailableField_whenCreateItem_thenThrowException() {

        itemDto.setAvailable(null);

        assertThrows(ValidationException.class, () -> itemService.create(user.getId(), itemDto));

    }

    @DisplayName("JUnit test for create method (negative scenario)")
    @Test
    public void givenItemDtoWithWrongUserId_whenCreateItem_thenThrowException() {

        given(userRepository.findById(-1L)).willThrow(IllegalArgumentException.class);

        assertThrows(IllegalArgumentException.class, () -> itemService.create(-1L, itemDto));

    }

    @DisplayName("JUnit test for update method")
    @Test
    public void givenItemObject_whenUpdateItem_thenReturnUpdatedItem() {

        given(itemRepository.findByIdAndOwnerId(item.getId(), user.getId())).willReturn(Optional.of(item));

        ItemDto dtoToUpdate = ItemDto.builder()
                .id(1L)
                .name("Щетка для британцев и не только")
                .description("Щетка для всех пород котов")
                .available(false)
                .build();

        item.setName("Щетка для британцев и не только");
        item.setAvailable(false);

        given(itemRepository.save(item)).willReturn(item);
        given(mapper.fromItem(item)).willReturn(dtoToUpdate);

        Optional<ItemDto> updateItem = itemService.update(user.getId(), item.getId(), dtoToUpdate);


        assertThat(updateItem.get().getName()).isEqualTo("Щетка для британцев и не только");
        assertThat(updateItem.get().getAvailable()).isFalse();
    }

    @DisplayName("JUnit test for update method (negative scenario)")
    @Test
    public void givenItemObjectWithWrongUserId_whenUpdateItem_thenThrowException() {

        given(itemRepository.findByIdAndOwnerId(1L, 3L)).willThrow(IllegalArgumentException.class);

        assertThrows(IllegalArgumentException.class, () -> itemService.update(3L, 1L, itemDto));

    }

    @DisplayName("JUnit test for getItemByIdForAllUser method")
    @Test
    public void givenItemDtoId_whenGetItemById_thenReturnItemObject() {

        given(itemRepository.findById(item.getId())).willReturn(Optional.of(item));
        given(mapper.fromItem(item)).willReturn(itemDto);
        item.setOwner(user);
        given(commentRepository.findAllByItemId(itemDto.getId())).willReturn(Collections.emptyList());
        given(bookingRepository.findAllByItemId(itemDto.getId())).willReturn(Collections.emptyList());

        Optional<ItemDto> itemFound = itemService.getItemByIdForAllUser(user.getId(), item.getId());

        assertThat(itemFound).isNotNull();
        assertThat(itemFound).isEqualTo(Optional.of(itemDto));
    }

    @DisplayName("JUnit test for getItemByIdForAllUser method (negative scenario)")
    @Test
    public void givenItemDtoIdWrong_whenGetItemById_thenThrowException() {

        given(itemRepository.findById(99L)).willThrow(IllegalArgumentException.class);

        assertThrows(IllegalArgumentException.class, () -> itemService.getItemByIdForAllUser(1L, 99L));

    }

    @DisplayName("JUnit test for findAllPageable method")
    @Test
    public void givenUserId_whenGetListItemDtoByOwnerId_thenReturnListOfItemDto() {

        Page<Item> page = new PageImpl<>(List.of(item));


        given(itemRepository.findAllByOwnerId(1L, PageRequest.of(0, 2))).willReturn(page);
        given(mapper.fromItem(item)).willReturn(itemDto);
        given(bookingRepository.findAllByItemId(itemDto.getId())).willReturn(Collections.emptyList());
        given(commentRepository.findAllByItemId(itemDto.getId())).willReturn(Collections.emptyList());

        List<ItemDto> itemDtoList = itemService.findAllPageable(1L, 0, 2);

        assertThat(itemDtoList.size()).isEqualTo(1);
        assertThat(itemDtoList.get(0)).isEqualTo(itemDto);

    }

    @DisplayName("JUnit test for findAllPageable method (negative scenario)")
    @Test
    public void givenIncorrectFrom_whenGetListItemDtoByOwnerId_thenThrowException() {

        int from = -1;
        int size = 1;

        assertThrows(ValidationException.class, () -> itemService.findAllPageable(1L, from, size));

    }

    @DisplayName("JUnit test for findAllPageable method (negative scenario)")
    @Test
    public void givenIncorrectSize_whenGetListItemDtoByOwnerId_thenThrowException() {

        int from = 1;
        int size = 0;

        assertThrows(ValidationException.class, () -> itemService.findAllPageable(1L, from, size));

    }

    @DisplayName("JUnit test for findAllPageable method (negative scenario)")
    @Test
    public void givenEmptyPage_whenGetListItemDtoByOwnerId_thenThrowException() {

        int from = 1;
        int size = 1;

        Page<Item> page = new PageImpl<>(Collections.emptyList());

        given(itemRepository.findAllByOwnerId(3L, PageRequest.of(from, size))).willReturn(page);

        assertThrows(IllegalArgumentException.class, () -> itemService.findAllPageable(3L, from, size));

    }

    @DisplayName("JUnit test for findItemByText method")
    @Test
    public void givenBlankText_whenGetListItemDtoByText_thenReturnEmptyListOfItemDto() {

        String text = "";
        int from = 1;
        int size = 1;

        List<ItemDto> retrievedDto = itemService.findItemByText(text, from, size);

        assertThat(retrievedDto).isEmpty();
    }

    @DisplayName("JUnit test for findItemByText method")
    @Test
    public void givenText_whenGetListItemDtoByText_thenReturnListOfItemDto() {

        String text = "Щетка для кота";
        int from = 0;
        int size = 1;

        Page<Item> page = new PageImpl<>(List.of(item));


        given(itemRepository.search(text, PageRequest.of(from, size))).willReturn(page);
        given(mapper.fromItem(item)).willReturn(itemDto);

        List<ItemDto> retrievedDto = itemService.findItemByText(text, from, size);

        assertThat(retrievedDto.size()).isEqualTo(1);
        assertThat(retrievedDto.get(0)).isEqualTo(itemDto);
    }

    @DisplayName("JUnit test for addCommentToItem method")
    @Test
    public void givenComment_whenAddCommentToItem_thenReturnCommentDto() {

        User booker = new User(
                3L,
                "Alla",
                "alla@36on.ru"
        );

        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("Хорошая щетка, моему коту подошла")
                .authorName("Alla")
                .build();

        Comment comment = new Comment(1L, "Хорошая щетка, моему коту подошла");


        given(userRepository.findById(booker.getId())).willReturn(Optional.of(booker));
        given(itemRepository.findById(item.getId())).willReturn(Optional.of(item));

        Booking booking = new Booking(1L, Timestamp.valueOf("2022-11-12 00:03:04"),
                Timestamp.valueOf("2022-11-13 00:03:04"), item, booker, BookingStatus.APPROVED);

        List<Booking> bookingList = new ArrayList<>();
        bookingList.add(booking);

        given(bookingRepository.findAllByItemIdAndBookerIdAndEndBefore(
                any(), any(), any())).willReturn(bookingList);

        given(commentMapper.fromDto(commentDto)).willReturn(comment);


        given(commentRepository.save(comment)).willReturn(comment);

        given(commentMapper.toDto(comment)).willReturn(commentDto);


        CommentDto savedComment = itemService.addCommentToItem(booker.getId(), item.getId(), commentDto);

        assertThat(savedComment).isNotNull();
        assertThat(savedComment.getAuthorName()).isEqualTo("Alla");
        assertThat(savedComment.getId()).isEqualTo(1L);
        assertThat(savedComment.getText()).isEqualTo("Хорошая щетка, моему коту подошла");


    }

    @DisplayName("JUnit test for addCommentToItem method (negative scenario)")
    @Test
    public void givenComment_whenAddCommentToItem_thenThrowException() {

        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("Хорошая щетка, моему коту подошла")
                .authorName("Alla")
                .build();


        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(itemRepository.findById(item.getId())).willReturn(Optional.of(item));

        given(bookingRepository.findAllByItemIdAndBookerIdAndEndBefore(
                any(), any(), any())).willReturn(Collections.emptyList());

        assertThrows(ValidationException.class, () -> itemService.addCommentToItem(1L, 1L, commentDto));

    }

}
