package ru.practicum.shareit.item;

import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.NewCommentRequest;
import ru.practicum.shareit.user.User;

import java.util.Collection;
import java.util.List;
import java.util.Map;


public interface ItemService {

    ItemDto save(Long userId, NewItemRequest newItemRequest);

    ItemDto findItemById(Long itemId);

    ItemDto update(Long userId, Long itemId, UpdateItemRequest updateItemRequest);

    void delete(Long userId, Long itemId);

    Item checkItem(Long itemId);

    Item checkAvailableItem(Long itemId);

    void checkOwner(Long userId, Item item);

    Collection<ItemDto> findAllByText(String text);

    Map<Long, List<CommentDto>> findAllCommentsByItemIds(Collection<Long> itemIds);

    CommentDto saveComment(User author, Item item, NewCommentRequest request);

    Map<Long, ItemDto> findItemDtoMapByIds(Collection<Long> itemIds);

}
