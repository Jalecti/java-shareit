package ru.practicum.shareit.request;


import java.util.Collection;
import java.util.Optional;

public interface ItemRequestStorage {

    Collection<ItemRequest> findAll();

    ItemRequest save(ItemRequest itemRequest);

    ItemRequest update(ItemRequest newItemRequest);

    void delete(Long itemRequestId);

    Optional<ItemRequest> findById(Long itemRequestId);

}
