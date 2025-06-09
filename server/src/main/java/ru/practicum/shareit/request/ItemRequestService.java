package ru.practicum.shareit.request;


import ru.practicum.shareit.item.ItemShortDto;

import java.util.Collection;


public interface ItemRequestService {
    ItemRequestDto save(Long userId, NewItemRequestRequest request);

    ItemRequestDto findItemRequestById(Long requestorId, Long itemRequestId, Collection<ItemShortDto> items);

    ItemRequest checkItemRequest(Long itemRequestId);

    ItemRequestDto findItemRequestById(Long requestorId, Long itemRequestId);

    Collection<ItemRequestDto> findAllByRequestorId(Long requestorId, Boolean isForRequestor);

}
