package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.utility.Storage;

import java.util.*;


@Repository
public class InMemoryItemRepository implements Storage<Item> {
    private Long counter;
    private final Map<Long, Item> items;

    public InMemoryItemRepository() {
        counter = 0L;
        items = new HashMap<>();
    }

    @Override
    public Collection<Item> findAll() {
        return new ArrayList<>(items.values());
    }

    public Collection<Item> findAllByUserId(Long userId) {
        return items.values()
                .stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .toList();
    }

    public Collection<Item> findAllByText(Long userId, String text) {
        return items.values()
                .stream()
                .filter(item -> (item.getAvailable() && (item.getName().toLowerCase().contains(text.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains(text.toLowerCase()))))
                .toList();
    }

    @Override
    public Item save(Item item) {
        item.setId(++counter);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item newItem) {
        Item itemToUpdate = items.get(newItem.getId());
        itemToUpdate.setName(newItem.getName());
        itemToUpdate.setDescription(newItem.getDescription());
        itemToUpdate.setAvailable(newItem.getAvailable());
        itemToUpdate.setOwner(newItem.getOwner());
        return itemToUpdate;
    }

    @Override
    public void delete(Long itemId) {
        items.remove(itemId);
    }

    @Override
    public Optional<Item> findById(Long itemId) {
        return Optional.ofNullable(items.get(itemId));
    }
}
