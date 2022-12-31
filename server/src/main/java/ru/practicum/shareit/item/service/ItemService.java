package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.CommentDto;
import ru.practicum.shareit.item.ItemDto;

import java.util.List;
import java.util.Optional;

public interface ItemService {

    ItemDto create(Long userId, ItemDto dto);

    Optional<ItemDto> update(Long userId, Long itemId, ItemDto dto);

    Optional<ItemDto> getItemByIdForAllUser(Long userId, Long itemId);


    List<ItemDto> findItemByText(String text, int from, int size);

    CommentDto addCommentToItem(Long userDto, Long itemDto, CommentDto dto);

    List<ItemDto> findAllPageable(Long userId, int from,int size);


}
