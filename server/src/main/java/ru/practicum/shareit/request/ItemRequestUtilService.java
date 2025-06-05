package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.ItemShortDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestUtilService {
    private final ItemRequestStorage itemRequestRepository;
    private final ItemRequestService itemRequestService;
    private final ItemService itemService;
    private final UserService userService;

    public ItemRequestDto findItemRequestById(Long requestorId, Long itemRequestId) {
        return itemRequestService.findItemRequestById(requestorId, itemRequestId,
                itemService.findAllByRequestId(itemRequestId));
    }

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
