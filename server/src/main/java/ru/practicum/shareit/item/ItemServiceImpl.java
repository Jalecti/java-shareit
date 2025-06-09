package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.ItemUnavailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.comment.*;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemRepository;
    private final UserService userService;
    private final CommentStorage commentRepository;


    @Transactional
    @Override
    public ItemDto save(Long userId, NewItemRequest newItemRequest, ItemRequest request) {
        User owner = userService.checkUser(userId);
        Item item = ItemMapper.mapToItem(newItemRequest, owner, request);
        item = itemRepository.save(item);
        log.info("Пользователь {} зарегистрировал предмет {} с ID: {}",
                owner.getEmail(),
                item.getName(),
                item.getId());
        return ItemMapper.mapToItemDto(item, new ArrayList<>());
    }


    @Override
    public ItemDto findItemById(Long itemId) {
        Item item = checkItem(itemId);
        return ItemMapper.mapToItemDto(item, findAllCommentsByItemId(itemId));
    }

    @Override
    public Collection<ItemDto> findAllByText(String text) {
        Collection<Item> items = itemRepository.findAllByText(text);
        List<Long> itemIds = items.stream().map(Item::getId).toList();
        Map<Long, List<CommentDto>> commentsMap = findAllCommentsByItemIds(itemIds);
        return items.stream()
                .map(item -> {
                    List<CommentDto> comments = commentsMap.getOrDefault(item.getId(), List.of());
                    return ItemMapper.mapToItemDto(item, comments);
                })
                .toList();
    }

    @Transactional
    @Override
    public ItemDto update(Long userId, Long itemId, UpdateItemRequest updateItemRequest) {
        Item itemToUpdate = checkItem(itemId);
        checkOwner(userId, itemToUpdate);
        updateItemRequest.setId(itemId);
        Item updatedItem = ItemMapper.updateItemFields(itemToUpdate, updateItemRequest);
        updatedItem = itemRepository.save(updatedItem);
        log.info("Предмет обновлен с ID: {}", itemId);
        return ItemMapper.mapToItemDto(updatedItem, findAllCommentsByItemId(updatedItem.getId()));
    }

    @Transactional
    @Override
    public void delete(Long userId, Long itemId) {
        Item itemToDelete = checkItem(itemId);
        checkOwner(userId, itemToDelete);
        String itemName = itemToDelete.getName();
        itemRepository.deleteById(itemId);
        log.info("Предмет {} с ID: {} удален", itemName, itemId);
    }

    @Override
    public Item checkItem(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> {
            log.error("Предмет не найден с ID: {}", itemId);
            return new NotFoundException("Предмет не найден с ID: " + itemId);
        });
    }

    @Override
    public Item checkAvailableItem(Long itemId) {
        Item item = checkItem(itemId);
        if (!item.getAvailable()) {
            log.error("Предмет с ID: {} недоступен для бронирования", itemId);
            throw new ItemUnavailableException("Предмет с ID: " + itemId + " недоступен для бронирования");
        }
        return item;
    }

    @Override
    public void checkOwner(Long userId, Item item) {
        if (!item.getOwner().getId().equals(userId)) {
            log.error("Пользователь с ID: {} не является владельцем предмета с ID: {}", userId, item.getId());
            throw new ForbiddenException("Пользователь с ID: " + userId +
                    " не является владельцем предмета с ID: " + item.getId());
        }
    }


    @Transactional
    @Override
    public CommentDto saveComment(User author, Item item, NewCommentRequest request) {
        Comment comment = CommentMapper.mapToComment(request, author, item);
        comment = commentRepository.save(comment);
        log.info("Пользователь {} оставил отзыв к предмету {} с ID: {}. Тело отзыва: {}",
                author.getEmail(),
                item.getName(),
                item.getId(),
                comment.getText());
        return CommentMapper.mapToCommentDto(comment);
    }

    @Override
    public Map<Long, List<CommentDto>> findAllCommentsByItemIds(Collection<Long> itemIds) {
        return commentRepository.findByItemIdIn(itemIds).stream()
                .map(CommentMapper::mapToCommentDto)
                .collect(Collectors.groupingBy(CommentDto::getItemId));
    }

    @Override
    public Map<Long, List<ItemShortDto>> findAllItemsByRequestIds(Collection<Long> requestIds) {
        return itemRepository.findByRequestIdIn(requestIds).stream()
                .map(ItemMapper::mapToShortDto)
                .collect(Collectors.groupingBy(ItemShortDto::getId));
    }

    private Collection<CommentDto> findAllCommentsByItemId(Long itemId) {
        return commentRepository.findByItemId(itemId)
                .stream()
                .map(CommentMapper::mapToCommentDto)
                .toList();
    }

    private Collection<CommentDto> findAllComments() {
        return commentRepository.findAll()
                .stream()
                .map(CommentMapper::mapToCommentDto)
                .toList();
    }

    @Override
    public Map<Long, ItemDto> findItemDtoMapByIds(Collection<Long> itemIds) {
        List<Item> items = itemRepository.findAllById(itemIds);
        Map<Long, List<CommentDto>> commentMap = findAllCommentsByItemIds(itemIds);

        return items.stream()
                .collect(Collectors.toMap(
                        Item::getId,
                        item -> ItemMapper.mapToItemDto(item, commentMap.getOrDefault(item.getId(), List.of()))
                ));
    }

    @Override
    public Collection<ItemShortDto> findAllByRequestId(Long requestId) {
        return itemRepository.findAllByRequestId(requestId).stream()
                .map(ItemMapper::mapToShortDto)
                .toList();
    }


}
