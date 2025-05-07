package ru.practicum.shareit.item;

import java.util.Collection;


public interface ItemService {

    ItemDto save(Long userId, NewItemRequest newItemRequest);

    Collection<ItemDto> findAllByUserId(Long userId);

    ItemDto findItemById(Long itemId);

    ItemDto update(Long userId, Long itemId, UpdateItemRequest updateItemRequest);

    void delete(Long userId, Long itemId);

    Item checkItem(Long itemId);

    Collection<ItemDto> findAllByText(Long userId, String text);
}
