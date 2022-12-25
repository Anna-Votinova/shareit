package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.Utils;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfo;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;

    private final ItemRepository itemRepository;

    private final ItemRequestMapper itemRequestMapper;

    private static final Sort SORT = Sort.by(Sort.Direction.DESC, "created");

    @Override
    public ItemRequestDto addNewRequest(Long userId, ItemRequestDto dto) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("User с id " + userId + " не найден"));
        ItemRequest iReq = itemRequestMapper.fromDto(dto);
        iReq.setRequester(user);
        iReq.setItems(new ArrayList<>());
        return itemRequestMapper.toDto(itemRequestRepository.save(iReq));
    }

    @Override
    public List<ItemRequestDto> findAll(Long userId) {
        checkUser(userId);

        List<ItemRequest> allUserReq = itemRequestRepository.findAllByRequesterId(userId, SORT);
        List<ItemRequestDto> resultList = new ArrayList<>();

        allUserReq.forEach(iReq -> {

            ItemRequestDto iReqResult = convertItemsAndRequests(iReq);
            resultList.add(iReqResult);

        });

        return resultList;

    }

    @Override
    public List<ItemRequestDto> findAllPageable(Long userId, int from, int size) {
        checkUser(userId);
        Utils.checkFromAndSize(from, size);
        Pageable pageable = PageRequest.of(from, size, SORT);
        List<ItemRequest> iReqPages = itemRequestRepository.findAll(pageable)
                .stream()
                .filter(i -> !i.getRequester().getId().equals(userId))
                .collect(Collectors.toList());

        if (iReqPages.isEmpty()) {
            return Collections.emptyList();
        }

        List<ItemRequestDto> resultList = new ArrayList<>();
        iReqPages.forEach(iReq -> {
            ItemRequestDto iReqResult = convertItemsAndRequests(iReq);
            resultList.add(iReqResult);

        });
        return resultList;
    }

    @Override
    public ItemRequestDto findItemRequestById(Long userId, Long itemReqId) {
        checkUser(userId);
        ItemRequest iReq = itemRequestRepository.findById(itemReqId).orElseThrow(
                () -> new IllegalArgumentException("Запроса с id " + itemReqId + " не существует"));
        return convertItemsAndRequests(iReq);
    }

    private ItemRequestDto convertItemsAndRequests(ItemRequest iReq) {

        ItemRequestDto iReqResult = itemRequestMapper.toDto(iReq);
        List<ItemRequestInfo> itemsForEveryIReq = new ArrayList<>();
        List<Item> itemsForRequest = itemRepository.findAllByRequestId(iReq.getId());

        if (itemsForRequest.isEmpty()) {
            iReqResult.setItems(Collections.emptyList());
        } else {

            itemsForRequest.forEach(item -> {

                    ItemRequestInfo info = ItemRequestInfo.builder()
                            .id(item.getId())
                            .name(item.getName())
                            .description(item.getDescription())
                            .available(item.getAvailable())
                            .requestId(iReq.getId())
                            .build();

                    itemsForEveryIReq.add(info);

                }

        );

        iReqResult.setItems(itemsForEveryIReq);

        }

        return iReqResult;

    }

    private void checkUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User с id " + userId + " не найден");
        }
    }

}
