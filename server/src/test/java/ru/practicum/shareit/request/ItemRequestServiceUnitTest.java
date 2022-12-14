package ru.practicum.shareit.request;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceUnitTest {

    @Mock
    ItemRequestRepository itemRequestRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    ItemRepository itemRepository;

    @Mock
    ItemRequestMapper itemRequestMapper;

    @InjectMocks
    ItemRequestServiceImpl itemRequestService;

    private ItemRequestDto dto;
    private ItemRequest request;
    private User user;

    private static final Sort SORT = Sort.by(Sort.Direction.DESC, "created");

    @BeforeEach
    public void setUp() {

    dto = ItemRequestDto.builder()
            .id(1L)
            .description("Хотел бы взять в аренду чесалку для кота")
            .created(LocalDateTime.parse("2022-12-18T20:00:57"))
            .build();

    request = ItemRequest.builder()
            .id(1L)
            .description("Хотел бы взять в аренду чесалку для кота")
            .created(Timestamp.valueOf(LocalDateTime.parse("2022-12-18T20:00:57")))
            .build();

    user = new User(1L, "Anna", "anna13@36on.ru");

    }

    @AfterEach
    public void verifyInteractions() {
        verifyNoMoreInteractions(
                userRepository,
                itemRepository,
                itemRequestRepository
        );
    }

    @DisplayName("JUnit test for addNewRequest method")
    @Test
    public void givenUserIdAndItemRequestDto_whenAddNewRequest_thenReturnItemRequestDto() {
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(itemRequestMapper.fromDto(dto)).willReturn(request);
        given(itemRequestRepository.save(request)).willReturn(request);
        request.setRequester(user);
        given(itemRequestMapper.toDto(request)).willReturn(dto);

        ItemRequestDto savedDto = itemRequestService.addNewRequest(request.getId(), dto);

        assertThat(savedDto).isNotNull();
        assertThat(savedDto).isEqualTo(dto);

        verify(userRepository, times(1))
                .findById(any());

        verify(itemRequestRepository, times(1))
                .save(any());
    }

    @DisplayName("JUnit test for addNewRequest method (negative scenario) ")
    @Test
    public void givenWrongItemRequestId_whenAddNewRequest_thenThrowException() {
        given(userRepository.findById(-1L)).willThrow(IllegalArgumentException.class);

        assertThrows(IllegalArgumentException.class,
                () -> itemRequestService.addNewRequest(-1L, dto));

        verify(userRepository, times(1))
                .findById(any());

        verify(itemRequestRepository, times(0))
                .save(any());

    }

    @DisplayName("JUnit test for findAll method")
    @Test
    public void givenUserId_whenFindAll_thenReturnListOfItemRequestDtoWithoutItems() {
        given(userRepository.existsById(anyLong())).willReturn(true);
        given(itemRequestRepository.findAllByRequesterId(1L, SORT)).willReturn(List.of(request));
        given(itemRequestMapper.toDto(request)).willReturn(dto);
        given(itemRepository.findAllByRequestId(request.getId())).willReturn(Collections.emptyList());

        List<ItemRequestDto> dtoList = itemRequestService.findAll(1L);

        assertThat(dtoList.size()).isEqualTo(1);
        assertThat(dtoList.get(0).getItems().size()).isEqualTo(0);


        verify(userRepository, times(1))
                .existsById(any());

        verify(itemRequestRepository, times(1))
                .findAllByRequesterId(any(), any());

        verify(itemRepository, times(1))
                .findAllByRequestId(any());

    }

    @DisplayName("JUnit test for findAll method")
    @Test
    public void givenUserId_whenFindAll_thenReturnListOfItemRequestDtoWithItems() {

        Item item = new Item(1L,"Щетка для кота","Щетка для всех пород котов",true);

        given(userRepository.existsById(anyLong())).willReturn(true);
        given(itemRequestRepository.findAllByRequesterId(1L, SORT)).willReturn(List.of(request));
        given(itemRequestMapper.toDto(request)).willReturn(dto);
        given(itemRepository.findAllByRequestId(request.getId())).willReturn(List.of(item));

        List<ItemRequestDto> dtoList = itemRequestService.findAll(1L);

        assertThat(dtoList.size()).isEqualTo(1);
        assertThat(dtoList.get(0).getItems().size()).isEqualTo(1);
        assertThat(dtoList.get(0).getItems().get(0).getDescription()).isEqualTo("Щетка для всех пород котов");

        verify(userRepository, times(1))
                .existsById(any());

        verify(itemRequestRepository, times(1))
                .findAllByRequesterId(any(), any());

        verify(itemRepository, times(1))
                .findAllByRequestId(any());

    }

    @DisplayName("JUnit test for findAll method")
    @Test
    public void givenUserId_whenFindAll_thenReturnSortedListOfItemRequestDtoWithoutItems() {

        User user2 = new User(2L, "Olga", "olga@gmail.com");

        ItemRequest newReq = ItemRequest.builder()
                .id(2L)
                .description("Нужна лопата")
                .created(Timestamp.valueOf(LocalDateTime.parse("2022-12-20T20:00:57")))
                .requester(user2)
                .build();

        ItemRequestDto dto2 = ItemRequestDto.builder()
                .id(2L)
                .description("Нужна лопата")
                .created(LocalDateTime.parse("2022-12-20T20:00:57"))
                .build();

        List<ItemRequest> requests = new ArrayList<>(Arrays.asList(request, newReq));

       requests = requests
                .stream()
                .sorted(Comparator.comparing(ItemRequest::getCreated).reversed())
                .collect(Collectors.toList());

        given(userRepository.existsById(anyLong())).willReturn(true);
        given(itemRequestRepository.findAllByRequesterId(1L, SORT)).willReturn(requests);
        given(itemRequestMapper.toDto(request)).willReturn(dto);
        given(itemRequestMapper.toDto(newReq)).willReturn(dto2);
        given(itemRepository.findAllByRequestId(request.getId())).willReturn(Collections.emptyList());
        given(itemRepository.findAllByRequestId(newReq.getId())).willReturn(Collections.emptyList());

        List<ItemRequestDto> sortedDtoList = itemRequestService.findAll(1L);

        assertThat(sortedDtoList.size()).isEqualTo(2);
        assertThat(sortedDtoList.get(0).getId()).isEqualTo(2);
        assertThat(sortedDtoList.get(1).getId()).isEqualTo(1);
        assertThat(sortedDtoList.get(0).getItems().size()).isEqualTo(0);
        assertThat(sortedDtoList.get(1).getItems().size()).isEqualTo(0);

        verify(userRepository, times(1))
                .existsById(any());

        verify(itemRequestRepository, times(1))
                .findAllByRequesterId(any(), any());

        verify(itemRepository, times(2))
                .findAllByRequestId(any());

    }

    @DisplayName("JUnit test for findAll method")
    @Test
    public void givenUserId_whenFindAll_thenReturnEmptyListOfItemRequestDto() {
        given(userRepository.existsById(anyLong())).willReturn(true);
        given(itemRequestRepository.findAllByRequesterId(1L, SORT)).willReturn(Collections.emptyList());

        List<ItemRequestDto> dtoList = itemRequestService.findAll(1L);

        assertThat(dtoList.size()).isEqualTo(0);

        verify(userRepository, times(1))
                .existsById(any());

        verify(itemRequestRepository, times(1))
                .findAllByRequesterId(any(), any());


    }


    @DisplayName("JUnit test for findAll method (negative scenario)")
    @Test
    public void givenUserId_whenFindAll_thenThrowException() {
        given(userRepository.existsById(anyLong())).willReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> itemRequestService.findAll(-1L));

        verify(userRepository, times(1))
                .existsById(any());

        verify(itemRequestRepository, times(0))
                .findAllByRequesterId(any(), any());

        verify(itemRepository, times(0))
                .findAllByRequestId(any());

    }

    @DisplayName("JUnit test for findAllPageable method")
    @Test
    public void givenUserIdFromAndSize_whenFindAllPageable_thenReturnListOfItemRequestDto() {

        User user2 = new User(2L, "Olga", "olga@gmail.com");
        User user3 = new User(3L, "Alla", "alla@gmail.com");

        ItemRequest newReq = ItemRequest.builder()
                .id(2L)
                .description("Нужна лопата")
                .created(Timestamp.valueOf(LocalDateTime.parse("2022-12-20T20:00:57")))
                .requester(user2)
                .build();

        ItemRequestDto dto2 = ItemRequestDto.builder()
                .id(2L)
                .description("Нужна лопата")
                .created(LocalDateTime.parse("2022-12-20T20:00:57"))
                .build();

        request.setRequester(user3);

        Page<ItemRequest> page = new PageImpl<>(List.of(newReq, request));

        given(userRepository.existsById(anyLong())).willReturn(true);
        given(itemRequestRepository.findAll(PageRequest.of(0, 5, SORT))).willReturn(page);
        given(itemRequestMapper.toDto(request)).willReturn(dto);
        given(itemRepository.findAllByRequestId(request.getId())).willReturn(Collections.emptyList());
        given(itemRequestMapper.toDto(newReq)).willReturn(dto2);
        given(itemRepository.findAllByRequestId(newReq.getId())).willReturn(Collections.emptyList());

        List<ItemRequestDto> finalList = itemRequestService.findAllPageable(user.getId(), 0, 5);

        assertThat(finalList.size()).isEqualTo(2);
        assertThat(finalList.get(0).getId()).isEqualTo(2);

        verify(userRepository, times(1))
                .existsById(any());

        verify(itemRequestRepository, times(1))
                .findAll(PageRequest.of(0, 5, SORT));

        verify(itemRepository, times(2))
                .findAllByRequestId(any());
    }


    @DisplayName("JUnit test for findAllPageable method")
    @Test
    public void givenUserIdFromAndSize_whenFindAllPageable_thenReturnEmptyListOfItemRequestDto() {

        Page<ItemRequest> page = new PageImpl<>(Collections.emptyList());
        given(userRepository.existsById(anyLong())).willReturn(true);
        given(itemRequestRepository.findAll(PageRequest.of(0, 5, SORT))).willReturn(page);

        List<ItemRequestDto> emptyList = itemRequestService.findAllPageable(user.getId(), 0, 5);

        assertThat(emptyList.size()).isEqualTo(0);

        verify(userRepository, times(1))
                .existsById(any());

        verify(itemRequestRepository, times(1))
                .findAll(PageRequest.of(0, 5, SORT));

        verify(itemRepository, times(0))
                .findAllByRequestId(any());
    }

    @DisplayName("JUnit test for findAllPageable method (negative scenario)")
    @Test
    public void givenUserId_whenFindAllPageable_thenThrowException() {
        given(userRepository.existsById(anyLong())).willReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> itemRequestService.findAllPageable(-1L, 1, 1));

        verify(userRepository, times(1))
                .existsById(any());

        verify(itemRequestRepository, times(0))
                .findAll(PageRequest.of(0, 5, SORT));

        verify(itemRepository, times(0))
                .findAllByRequestId(any());

    }


    @DisplayName("JUnit test for findItemRequestById method")
    @Test
    public void givenUserIdAndItemRequestId_whenFindItemRequestById_thenReturnItemRequestDto() {
        given(userRepository.existsById(anyLong())).willReturn(true);
        given(itemRequestRepository.findById(request.getId())).willReturn(Optional.ofNullable(request));
        given(itemRequestMapper.toDto(request)).willReturn(dto);
        given(itemRepository.findAllByRequestId(request.getId())).willReturn(Collections.emptyList());

        ItemRequestDto findDto = itemRequestService.findItemRequestById(user.getId(), request.getId());

        assertThat(findDto).isNotNull();
        assertThat(findDto.getId()).isEqualTo(1);

        verify(userRepository, times(1))
                .existsById(any());

        verify(itemRequestRepository, times(1))
                .findById(any());

        verify(itemRepository, times(1))
                .findAllByRequestId(any());

    }

    @DisplayName("JUnit test for findItemRequestById method (negative scenario) ")
    @Test
    public void givenWrongItemRequestId_whenFindItemRequestById_thenThrowException() {
        given(userRepository.existsById(anyLong())).willReturn(true);

        given(itemRequestRepository.findById(-1L)).willThrow(IllegalArgumentException.class);

        assertThrows(IllegalArgumentException.class,
                () -> itemRequestService.findItemRequestById(user.getId(), -1L));

        verify(userRepository, times(1))
                .existsById(any());

        verify(itemRequestRepository, times(1))
                .findById(any());

        verify(itemRepository, times(0))
                .findAllByRequestId(any());

    }

    @DisplayName("JUnit test for FindItemRequestById method (negative scenario)")
    @Test
    public void givenUserId_whenFindItemRequestById_thenThrowException() {
        given(userRepository.existsById(anyLong())).willReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> itemRequestService.findItemRequestById(-1L, 1L));

        verify(userRepository, times(1))
                .existsById(any());

        verify(itemRequestRepository, times(0))
                .findById(any());

        verify(itemRepository, times(0))
                .findAllByRequestId(any());

    }

}
