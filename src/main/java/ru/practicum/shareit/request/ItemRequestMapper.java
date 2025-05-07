package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ItemRequestMapper {
    public static ItemRequestDto mapToItemRequestDto(ItemRequest itemRequest) {
        ItemRequestDto itemRequestDto = new ItemRequestDto();

        itemRequestDto.setId(itemRequest.getId());
        itemRequestDto.setDescription(itemRequest.getDescription());
        itemRequestDto.setRequestor(itemRequest.getRequestor());
        itemRequestDto.setCreated(itemRequest.getCreated());

        return itemRequestDto;
    }

    public static ItemRequest mapToItemRequest(NewItemRequestRequest request) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(request.getDescription());
        itemRequest.setRequestor(request.getRequestor());
        itemRequest.setCreated(request.getCreated());
        return itemRequest;
    }

    public static ItemRequest updateItemRequestFields(ItemRequest itemRequest, UpdateItemRequestRequest request) {
        if (request.hasDescription()) {
            itemRequest.setDescription(request.getDescription());
        }
        return itemRequest;
    }
}
