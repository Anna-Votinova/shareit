package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto addNewRequest(Long userId, ItemRequestDto dto);

    List<ItemRequestDto> findAll(Long userId);

    List<ItemRequestDto> findAllPageable(Long userId, int from,int size);

    ItemRequestDto findItemRequestById(Long userId, Long itemReqId);
}
