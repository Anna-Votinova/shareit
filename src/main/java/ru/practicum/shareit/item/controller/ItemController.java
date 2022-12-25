package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.Optional;


@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> getAllItemsOfUser(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                           @PositiveOrZero @RequestParam(defaultValue = "0") final int from,
                                           @Positive @RequestParam(defaultValue = "10") final int size) {
        return itemService.findAllPageable(userId, from, size);
    }

    @GetMapping("{itemId}")
    public Optional<ItemDto> getItemById(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                         @Positive @PathVariable Long itemId) {
        return itemService.getItemByIdForAllUser(userId, itemId);
    }

    @GetMapping("search")
    public List<ItemDto> getItemByText(@RequestParam String text,
                                       @PositiveOrZero @RequestParam(defaultValue = "0") final int from,
                                       @Positive @RequestParam(defaultValue = "10") final int size) {
        return itemService.findItemByText(text, from, size);
    }

    @PostMapping
    public ItemDto addItemToUser(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                 @Valid @RequestBody ItemDto itemDto) {
        return itemService.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public Optional<ItemDto> updateItem(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                        @Positive @PathVariable Long itemId, @RequestBody ItemDto itemDto) {
        return itemService.update(userId, itemId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addCommentToItem(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                       @Positive @PathVariable Long itemId, @Valid @RequestBody CommentDto dto) {
        return itemService.addCommentToItem(userId, itemId, dto);
    }
}
