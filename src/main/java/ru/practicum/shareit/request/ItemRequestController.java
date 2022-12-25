package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto addNewRequest(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                        @Valid @RequestBody ItemRequestDto dto) {
        return itemRequestService.addNewRequest(userId, dto);
    }

    @GetMapping
    public List<ItemRequestDto> findAllItemRequests(@Positive @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.findAll(userId);
    }

    @GetMapping("all")
    public List<ItemRequestDto> findAllPageable(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                                @PositiveOrZero @RequestParam(defaultValue = "0") final int from,
                                                @Positive @RequestParam(defaultValue = "10") final int size) {

        return itemRequestService.findAllPageable(userId, from, size);

    }

    @GetMapping("{requestId}")
    public ItemRequestDto findItemRequestById(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                              @Positive @PathVariable Long requestId) {
        return itemRequestService.findItemRequestById(userId, requestId);

    }



}
