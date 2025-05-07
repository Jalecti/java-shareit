package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.ArrayList;
import java.util.Collection;


@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemRepository;
    private final UserService userService;

    @Override
    public ItemDto save(Long userId, NewItemRequest newItemRequest) {
        User owner = userService.checkUser(userId);
        Item item = ItemMapper.mapToItem(newItemRequest, owner);
        item = itemRepository.save(item);
        log.info("Пользователь {} зарегистрировал предмет {} с ID: {}",
                owner.getEmail(),
                item.getName(),
                item.getId());
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public Collection<ItemDto> findAllByUserId(Long userId) {
        return itemRepository.findAllByUserId(userId)
                .stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    @Override
    public ItemDto findItemById(Long itemId) {
        return ItemMapper.mapToItemDto(checkItem(itemId));
    }

    public Collection<ItemDto> findAllByText(Long userId, String text) {
        if (text.isBlank()) return new ArrayList<>();
        return itemRepository.findAllByText(userId, text)
                .stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    @Override
    public ItemDto update(Long userId, Long itemId, UpdateItemRequest updateItemRequest) {
        Item itemToUpdate = checkItem(itemId);
        checkOwner(userId, itemToUpdate);
        updateItemRequest.setId(itemId);
        Item updatedItem = ItemMapper.updateItemFields(itemToUpdate, updateItemRequest);
        updatedItem = itemRepository.update(updatedItem);
        log.info("Предмет обновлен с ID: {}", itemId);
        return ItemMapper.mapToItemDto(updatedItem);
    }

    @Override
    public void delete(Long userId, Long itemId) {
        Item itemToDelete = checkItem(itemId);
        checkOwner(userId, itemToDelete);
        String itemName = itemToDelete.getName();
        itemRepository.delete(itemId);
        log.info("Предмет {} с ID: {} удален", itemName, itemId);
    }

    private Item checkItem(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> {
            log.error("Предмет не найден с ID: {}", itemId);
            return new NotFoundException("Предмет не найден с ID: " + itemId);
        });
    }

    private void checkOwner(Long userId, Item item) {
        if (!item.getOwner().getId().equals(userId)) {
            log.error("Пользователь с ID: {} не является владельцем предмета с ID: {}", userId, item.getId());
            throw new ForbiddenException("Пользователь с ID: " + userId +
                    " не является владельцем предмета с ID: " + item.getId());
        }
    }

}
