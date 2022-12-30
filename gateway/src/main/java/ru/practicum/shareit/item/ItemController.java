package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.ValidationException;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;

    private static final String USER_ID = "X-Sharer-User-Id";

    @GetMapping
    public ResponseEntity<Object> getAllItemsOfUser(@Positive @RequestHeader(USER_ID) Long userId,
                                                    @PositiveOrZero @RequestParam(defaultValue = "0") final int from,
                                                    @Positive @RequestParam(defaultValue = "10") final int size) {
        return itemClient.getItems(userId, from, size);
    }

    @GetMapping("{itemId}")
    public ResponseEntity<Object> getItemById(@Positive @RequestHeader(USER_ID) Long userId,
                                         @Positive @PathVariable Long itemId) {
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping("search")
    public ResponseEntity<Object> getItemByText(@RequestHeader(USER_ID) Long userId,
                                                @RequestParam String text,
                                       @PositiveOrZero @RequestParam(defaultValue = "0") final int from,
                                       @Positive @RequestParam(defaultValue = "10") final int size) {
        return itemClient.getItemsByText(text, userId, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> addItemToUser(@Positive @RequestHeader(USER_ID) Long userId,
                                 @Valid @RequestBody ItemDto itemDto) {
        if (itemDto.getAvailable() == null) {
            throw new ValidationException("Все поля должны быть заполнены");
        }
        return itemClient.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@Positive @RequestHeader(USER_ID) Long userId,
                                        @Positive @PathVariable Long itemId, @RequestBody ItemDto itemDto) {
        return itemClient.update(userId, itemId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addCommentToItem(@Positive @RequestHeader(USER_ID) Long userId,
                                       @Positive @PathVariable Long itemId, @Valid @RequestBody CommentDto dto) {
        return itemClient.addCommentToItem(userId, itemId, dto);
    }


}
