package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.CommentDto;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemDtoLastNextBooking;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.validation.ValidationException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    private final BookingRepository bookingRepository;

    private final CommentRepository commentRepository;

    private final ItemRequestRepository itemRequestRepository;
    private final ItemMapper mapper;

    private final CommentMapper commentMapper;


    @Override
    public ItemDto create(Long userId, ItemDto dto) {

        User user = userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("Юзер с id " + userId + " не существует"));

        Item item = mapper.fromDto(dto);
        item.setOwner(user);
        if (dto.getRequestId() != null) {
            ItemRequest iReq = itemRequestRepository.findById(dto.getRequestId()).orElseThrow(
                    () -> new IllegalArgumentException("Запроса с id " + dto.getRequestId() + " не существует"));
            item.setRequest(iReq);
        }

        return mapper.fromItem(itemRepository.save(item));

    }

    @Override
    public Optional<ItemDto> update(Long userId, Long itemId, ItemDto itemDto) {
        Item itemFromData = itemRepository.findByIdAndOwnerId(itemId, userId).orElseThrow(
                () -> new IllegalArgumentException("Item с id " + itemId + " не найден"));
        Optional.ofNullable(itemDto.getName()).ifPresent(itemFromData::setName);
        Optional.ofNullable(itemDto.getDescription()).ifPresent(itemFromData::setDescription);
        Optional.ofNullable(itemDto.getAvailable()).ifPresent(itemFromData::setAvailable);
        return Optional.of(mapper.fromItem(itemRepository.save(itemFromData)));

    }

    @Override
    public Optional<ItemDto> getItemByIdForAllUser(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new IllegalArgumentException("Item с id " + itemId + " не найден"));
        ItemDto itemDto = mapper.fromItem(item);
        if (item.getOwner().getId().equals(userId)) {
            getAndSetLastAndNextBooking(itemDto);
        }
        return Optional.of(addCommentsToItemDto(itemDto));
    }

    @Override
    public List<ItemDto> findAllPageable(Long userId, int from, int size) {

        Pageable pageable = PageRequest.of(from, size);
        Page<Item> userItems = itemRepository.findAllByOwnerId(userId, pageable);

        if (userItems.isEmpty()) {
            throw new IllegalArgumentException("Ни один айтем не найден");
        }

        List<ItemDto> resultList = new ArrayList<>();

        userItems.forEach(i -> {
            ItemDto dto = getAndSetLastAndNextBooking(mapper.fromItem(i));
            resultList.add(addCommentsToItemDto(dto));
        });

        return resultList.stream()
                .sorted(Comparator.comparing(ItemDto::getId))
                .collect(Collectors.toList());
    }

    private ItemDto getAndSetLastAndNextBooking(ItemDto dto) {
        Timestamp currentTime = Timestamp.from(Instant.now());
        List<Booking> bookings = bookingRepository.findAllByItemId(dto.getId());

        Optional<Booking> lastBooking = bookings
                .stream()
                .filter(b -> b.getStatus().equals(BookingStatus.APPROVED) && (b.getStart().equals(currentTime)
                        || b.getStart().before(currentTime)))
                .max(Comparator.comparing(Booking::getStart));

        Optional<Booking> nextBooking = bookings
                .stream()
                .filter(b -> b.getStart().after(currentTime))
                .min(Comparator.comparing(Booking::getStart));

        lastBooking.ifPresent(booking -> dto.setLastBooking(ItemDtoLastNextBooking.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .build()));

        nextBooking.ifPresent(booking -> dto.setNextBooking(ItemDtoLastNextBooking.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .build()));

        return dto;

    }

    private ItemDto addCommentsToItemDto(ItemDto dto) {

        Set<CommentDto> commentDto =
                commentRepository.findAllByItemId(dto.getId())
                        .stream()
                        .map(commentMapper::toDto)
                        .collect(Collectors.toSet());

       dto.setComments(commentDto);
       return dto;
    }

    @Override
    public List<ItemDto> findItemByText(String text, int from, int size) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }

        return itemRepository.search(text, PageRequest.of(from, size))
                .stream()
                .map(mapper::fromItem)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto addCommentToItem(Long userId, Long itemId, CommentDto dto) {
        User user  = userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("User с id " + userId + " не найден"));
        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new IllegalArgumentException("Item с id " + itemId + " не найден"));

        List<Booking> booking = bookingRepository.findAllByItemIdAndBookerIdAndEndBeforeAndStatus(
                itemId, userId, Timestamp.from(Instant.now()), BookingStatus.APPROVED);

        if (booking.isEmpty()) {
            throw new ValidationException("Юзер " + userId + " не может оставить комментарий, " +
                    "так как еще не бронировал вещь " + itemId);
        }
        Comment comment = commentMapper.fromDto(dto);
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(Timestamp.valueOf(LocalDateTime.now()));
        comment = commentRepository.save(comment);
        return commentMapper.toDto(comment);
    }



}
