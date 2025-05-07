package ru.practicum.shareit.request;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.utility.Storage;

import java.util.*;


@Repository
public class InMemoryItemRequestRepository implements Storage<ItemRequest> {
    private Long counter;
    private final Map<Long, ItemRequest> itemRequests;

    public InMemoryItemRequestRepository() {
        counter = 0L;
        itemRequests = new HashMap<>();
    }

    @Override
    public Collection<ItemRequest> findAll() {
        return new ArrayList<>(itemRequests.values());
    }

    @Override
    public ItemRequest save(ItemRequest itemRequest) {
        itemRequest.setId(++counter);
        itemRequests.put(itemRequest.getId(), itemRequest);
        return itemRequest;
    }

    @Override
    public ItemRequest update(ItemRequest newItemRequest) {
        ItemRequest itemRequestToUpdate = itemRequests.get(newItemRequest.getId());
        itemRequestToUpdate.setDescription(newItemRequest.getDescription());
        return itemRequestToUpdate;
    }

    @Override
    public void delete(Long itemRequestId) {
        itemRequests.remove(itemRequestId);
    }

    @Override
    public Optional<ItemRequest> findById(Long itemRequestId) {
        return Optional.ofNullable(itemRequests.get(itemRequestId));
    }
}
