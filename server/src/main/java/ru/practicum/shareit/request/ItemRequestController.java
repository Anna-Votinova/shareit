package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    private static final String USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestDto addNewRequest(@RequestHeader(USER_ID) Long userId,
                                        @RequestBody ItemRequestDto dto) {
        return itemRequestService.addNewRequest(userId, dto);
    }

    @GetMapping
    public List<ItemRequestDto> findAllItemRequests(@RequestHeader(USER_ID) Long userId) {
        return itemRequestService.findAll(userId);
    }

    @GetMapping("all")
    public List<ItemRequestDto> findAllPageable(@RequestHeader(USER_ID) Long userId,
                                                @RequestParam(defaultValue = "0") final int from,
                                                @RequestParam(defaultValue = "10") final int size) {

        return itemRequestService.findAllPageable(userId, from, size);

    }

    @GetMapping("{requestId}")
    public ItemRequestDto findItemRequestById(@RequestHeader(USER_ID) Long userId,
                                              @PathVariable Long requestId) {
        return itemRequestService.findItemRequestById(userId, requestId);

    }



}
