package ru.practicum.shareit.itemRequest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.itemRequest.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.Constants.USER_ID;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;


    @PostMapping
    public ResponseEntity<Object> addNewRequest(@Positive @RequestHeader(USER_ID) Long userId,
                                                @Valid @RequestBody ItemRequestDto dto) {
        return itemRequestClient.create(userId, dto);
    }

    @GetMapping
    public ResponseEntity<Object> findAllItemRequests(@Positive @RequestHeader(USER_ID) Long userId) {
        return itemRequestClient.getItemRequests(userId);
    }

    @GetMapping("all")
    public ResponseEntity<Object> findAllPageable(@Positive @RequestHeader(USER_ID) Long userId,
                                                @PositiveOrZero @RequestParam(defaultValue = "0") final int from,
                                                @Positive @RequestParam(defaultValue = "10") final int size) {

        return itemRequestClient.getItemRequestsPage(userId, from, size);

    }

    @GetMapping("{requestId}")
    public ResponseEntity<Object> findItemRequestById(@Positive @RequestHeader(USER_ID) Long userId,
                                              @Positive @PathVariable Long requestId) {
        return itemRequestClient.getItemRequest(userId, requestId);
    }
}
