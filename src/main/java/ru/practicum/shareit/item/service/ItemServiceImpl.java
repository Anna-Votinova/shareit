package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoLastNextBooking;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
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
    private final ItemMapper mapper;

    @Override
    public ItemDto create(Long userId, ItemDto dto) {
        if (dto.getAvailable() == null) {
            throw new ValidationException("Все поля должны быть заполнены");
        } else {
            Optional<User> user = userRepository.findById(userId);
            if (user.isPresent()) {
                Item item = mapper.fromDto(dto);
                item.setOwner(user.get());
                return mapper.fromItem(itemRepository.save(item));
            }
            throw new IllegalArgumentException("Юзер с id " + userId + " не существует");
        }
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
    public List<ItemDto> findAll(Long userId) {

        List<Item> userItems = itemRepository.findAllByOwnerId(userId);
        List<ItemDto> resultList = new ArrayList<>();

        userItems.forEach(i -> {
            ItemDto dto = getAndSetLastAndNextBooking(mapper.fromItem(i));
            resultList.add(addCommentsToItemDto(dto));
        });

        if (resultList.isEmpty()) {
            throw new IllegalArgumentException("Ни один айтем не найден");
        }

        return resultList;
    }

    private ItemDto getAndSetLastAndNextBooking(ItemDto dto) {
        Timestamp currentTime = Timestamp.from(Instant.now());
        List<Booking> bookings = bookingRepository.findAllByItemId(dto.getId());

        Optional<Booking> lastBooking = bookings
                .stream()
                .filter(b -> b.getStart().equals(currentTime)
                        || b.getStart().before(currentTime))
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
                        .map(CommentMapper::toDto)
                        .collect(Collectors.toSet());

       dto.setComments(commentDto);
       return dto;
    }

    @Override
    public List<ItemDto> findItemByText(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.search(text)
                .stream()
                .map(mapper::fromItem)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto addCommentToItem(Long userId, Long itemId, CommentDto dto) {
        User user  = (userRepository.findById(userId)).orElseThrow(
                () -> new IllegalArgumentException("User с id " + userId + " не найден"));
        Item item = (itemRepository.findById(itemId)).orElseThrow(
                () -> new IllegalArgumentException("Item с id " + itemId + " не найден"));

        List<Booking> booking = bookingRepository.findAllByItemIdAndBookerIdAndEndBefore(
                itemId, userId, Timestamp.from(Instant.now()));

        if (booking.isEmpty()) {
            throw new ValidationException("Юзер " + userId + " не может оставить комментарий, " +
                    "так как еще не бронировал вещь " + itemId);
        }
        Comment comment = CommentMapper.fromDto(dto);
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(Timestamp.valueOf(LocalDateTime.now()));
        comment = commentRepository.save(comment);
        return CommentMapper.toDto(comment);
    }



}
