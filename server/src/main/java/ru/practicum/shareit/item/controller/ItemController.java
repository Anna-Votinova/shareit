package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.CommentDto;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;
import java.util.Optional;


@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    private static final String USER_ID = "X-Sharer-User-Id";

    @GetMapping
    public List<ItemDto> getAllItemsOfUser(@RequestHeader(USER_ID) Long userId,
                                           @RequestParam(defaultValue = "0") final int from,
                                           @RequestParam(defaultValue = "10") final int size) {
        return itemService.findAllPageable(userId, from, size);
    }

    @GetMapping("{itemId}")
    public Optional<ItemDto> getItemById(@RequestHeader(USER_ID) Long userId,
                                         @PathVariable Long itemId) {
        return itemService.getItemByIdForAllUser(userId, itemId);
    }

    @GetMapping("search")
    public List<ItemDto> getItemByText(@RequestParam String text,
                                       @RequestParam(defaultValue = "0") final int from,
                                       @RequestParam(defaultValue = "10") final int size) {
        return itemService.findItemByText(text, from, size);
    }

    @PostMapping
    public ItemDto addItemToUser(@RequestHeader(USER_ID) Long userId,
                                 @RequestBody ItemDto itemDto) {
        return itemService.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public Optional<ItemDto> updateItem(@RequestHeader(USER_ID) Long userId,
                                        @PathVariable Long itemId, @RequestBody ItemDto itemDto) {
        return itemService.update(userId, itemId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addCommentToItem(@RequestHeader(USER_ID) Long userId,
                                       @PathVariable Long itemId, @RequestBody CommentDto dto) {
        return itemService.addCommentToItem(userId, itemId, dto);
    }
}
