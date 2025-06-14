package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.BookingShortDto;
import ru.practicum.shareit.exception.UnavailableToCommentException;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.NewCommentRequest;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;


@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemUtilService {
    private final UserService userService;
    private final BookingService bookingService;
    private final ItemService itemService;
    private final ItemStorage itemRepository;
    private final ItemRequestService itemRequestService;

    @Transactional
    public ItemDto save(Long userId, NewItemRequest newItemRequest) {
        Long requestId = newItemRequest.getRequestId();
        return requestId == null
                ? itemService.save(userId, newItemRequest, null)
                : itemService.save(userId, newItemRequest, itemRequestService.checkItemRequest(requestId));
    }

    public Collection<ItemDto> findAllByOwnerId(Long ownerId) {
        Collection<Item> items = itemRepository.findAllByOwnerId(ownerId);
        List<Long> itemIds = items.stream().map(Item::getId).toList();
        Map<Long, List<CommentDto>> commentsMap = itemService.findAllCommentsByItemIds(itemIds);
        Map<Long, BookingShortDto> prevsMap = bookingService.findAllPrevsByItemIds(itemIds);
        Map<Long, BookingShortDto> nextsMap = bookingService.findAllNextsByItemIds(itemIds);
        return items.stream()
                .map(item -> {
                    List<CommentDto> comments = commentsMap.getOrDefault(item.getId(), List.of());
                    BookingShortDto prev = prevsMap.get(item.getId());
                    BookingShortDto next = nextsMap.get(item.getId());
                    return ItemMapper.mapToItemDto(item, comments, prev, next);
                })
                .toList();
    }

    @Transactional
    public CommentDto comment(Long authorId, Long itemId, NewCommentRequest request) {
        User author = userService.checkUser(authorId);
        Item item = itemService.checkItem(itemId);
        checkItemBooker(author, item);
        return itemService.saveComment(author, item, request);
    }

    private void checkItemBooker(User author, Item item) {
        Boolean hasBooking = bookingService.existsByBookerIdAndItemIdAndEndBefore(
                author.getId(),
                item.getId(),
                LocalDateTime.now());
        if (!hasBooking) {
            log.error("Букер {} никогда не брал в аренду предмет {}, либо срок аренды еще не прошел",
                    author.getEmail(),
                    item.getName());
            throw new UnavailableToCommentException("Букер " + author.getEmail()
                    + " никогда не брал в аренду предмет " + item.getName()
                    + ", либо срок аренды еще не прошел");
        }
    }


}

