package ru.practicum.shareit.item;

import java.util.Collection;
import java.util.Optional;

public interface ItemStorage {

    Collection<Item> findAll();

    Item save(Item item);

    Item update(Item newItem);

    void delete(Long itemId);

    Optional<Item> findById(Long itemId);

    Collection<Item> findAllByUserId(Long userId);

    Collection<Item> findAllByText(Long userId, String text);
}