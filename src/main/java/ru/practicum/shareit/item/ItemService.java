package ru.practicum.shareit.item;

import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.NewCommentRequest;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.util.Collection;
import java.util.List;
import java.util.Map;


public interface ItemService {

    ItemDto save(Long userId, NewItemRequest newItemRequest, ItemRequest request);

    ItemDto findItemById(Long itemId);

    ItemDto update(Long userId, Long itemId, UpdateItemRequest updateItemRequest);

    void delete(Long userId, Long itemId);

    Item checkItem(Long itemId);

    Item checkAvailableItem(Long itemId);

    void checkOwner(Long userId, Item item);

    Collection<ItemDto> findAllByText(String text);

    Map<Long, List<CommentDto>> findAllCommentsByItemIds(Collection<Long> itemIds);

    Map<Long, List<ItemShortDto>> findAllItemsByRequestIds(Collection<Long> requestsIds);

    CommentDto saveComment(User author, Item item, NewCommentRequest request);

    Map<Long, ItemDto> findItemDtoMapByIds(Collection<Long> itemIds);

    Collection<ItemShortDto> findAllByRequestId(Long requestId);
}
