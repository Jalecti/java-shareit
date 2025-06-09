package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.ItemShortDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestStorage itemRequestRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Transactional
    @Override
    public ItemRequestDto save(Long userId, NewItemRequestRequest request) {
        User requestor = userService.checkUser(userId);
        ItemRequest itemRequest = ItemRequestMapper.mapToItemRequest(request, requestor);
        itemRequest = itemRequestRepository.save(itemRequest);
        log.info("Пользователь {} создал запрос с ID: {} на предмет с описанием: {}",
                requestor.getEmail(),
                itemRequest.getId(),
                itemRequest.getDescription());
        return ItemRequestMapper.mapToItemRequestDto(
                itemRequest,
                UserMapper.mapToUserDto(requestor),
                new ArrayList<>());
    }

    @Override
    public ItemRequestDto findItemRequestById(Long requestorId, Long itemRequestId, Collection<ItemShortDto> items) {
        userService.checkUser(requestorId);
        ItemRequest itemRequest = checkItemRequest(itemRequestId);
        return ItemRequestMapper.mapToItemRequestDto(
                itemRequest,
                UserMapper.mapToUserDto(itemRequest.getRequestor()),
                items);
    }

    @Override
    public ItemRequest checkItemRequest(Long itemRequestId) {
        return itemRequestRepository.findById(itemRequestId).orElseThrow(() -> {
            log.error("Запрос не найден с ID: {}", itemRequestId);
            return new NotFoundException("Запрос не найден с ID: " + itemRequestId);
        });
    }

    @Override
    public ItemRequestDto findItemRequestById(Long requestorId, Long itemRequestId) {
        return findItemRequestById(requestorId, itemRequestId,
                itemService.findAllByRequestId(itemRequestId));
    }

    @Override
    public Collection<ItemRequestDto> findAllByRequestorId(Long requestorId, Boolean isForRequestor) {
        User requestor = userService.checkUser(requestorId);
        UserDto requestorDto = UserMapper.mapToUserDto(requestor);
        Collection<ItemRequest> requests = isForRequestor
                ? itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(requestorId)
                : itemRequestRepository.findAllByRequestorIdNotOrderByCreatedDesc(requestorId);
        Map<Long, List<ItemShortDto>> requestToItemsMap = itemService
                .findAllItemsByRequestIds(requests.stream().map(ItemRequest::getId).toList());
        return requests
                .stream()
                .map(request -> ItemRequestMapper.mapToItemRequestDto(request, requestorDto,
                        requestToItemsMap.get(request.getId())))
                .toList();
    }
}
