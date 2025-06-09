package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.BookingShortDto;
import ru.practicum.shareit.exception.UnavailableToCommentException;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.NewCommentRequest;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ItemUtilServiceTest {
    @Mock
    private UserService userService;
    @Mock
    private BookingService bookingService;
    @Mock
    private ItemService itemService;
    @Mock
    private ItemStorage itemRepository;
    @Mock
    private ItemRequestService itemRequestService;
    @InjectMocks
    private ItemUtilService itemUtilService;

    @Test
    void save_whenRequestIdIsNull_thenDelegatesWithoutRequest() {
        Long userId = 1L;
        NewItemRequest newItemRequest = new NewItemRequest("item", "desc", true, null);
        ItemDto expected = new ItemDto();

        Mockito.when(itemService.save(userId, newItemRequest, null)).thenReturn(expected);

        ItemDto result = itemUtilService.save(userId, newItemRequest);

        assertSame(expected, result);
        Mockito.verify(itemService).save(userId, newItemRequest, null);
    }

    @Test
    void save_whenRequestIdExists_thenDelegatesWithRequest() {
        Long userId = 1L;
        Long requestId = 10L;
        NewItemRequest newItemRequest = new NewItemRequest("item", "desc", true, requestId);
        ItemRequest request = new ItemRequest(requestId, null, null, null);
        ItemDto expected = new ItemDto();

        Mockito.when(itemRequestService.checkItemRequest(requestId)).thenReturn(request);
        Mockito.when(itemService.save(userId, newItemRequest, request)).thenReturn(expected);

        ItemDto result = itemUtilService.save(userId, newItemRequest);

        assertSame(expected, result);
        Mockito.verify(itemRequestService).checkItemRequest(requestId);
        Mockito.verify(itemService).save(userId, newItemRequest, request);
    }

    @Test
    void comment_whenValid_thenReturnsSavedComment() {
        Long authorId = 2L;
        Long itemId = 3L;
        User author = new User(authorId, "name", "email@example.com");
        Item item = new Item(itemId, "item", "desc", true, new User(), null);
        NewCommentRequest request = new NewCommentRequest("text");
        CommentDto expected = new CommentDto();

        Mockito.when(userService.checkUser(authorId)).thenReturn(author);
        Mockito.when(itemService.checkItem(itemId)).thenReturn(item);
        Mockito.when(bookingService.existsByBookerIdAndItemIdAndEndBefore(Mockito.eq(authorId), Mockito.eq(itemId), Mockito.any()))
                .thenReturn(true);
        Mockito.when(itemService.saveComment(author, item, request)).thenReturn(expected);

        CommentDto result = itemUtilService.comment(authorId, itemId, request);

        assertSame(expected, result);
        Mockito.verify(itemService).saveComment(author, item, request);
    }

    @Test
    void comment_whenNoPastBooking_thenThrowsException() {
        Long authorId = 2L;
        Long itemId = 3L;
        User author = new User(authorId, "name", "email@example.com");
        Item item = new Item(itemId, "item", "desc", true, new User(), null);
        NewCommentRequest request = new NewCommentRequest("text");

        Mockito.when(userService.checkUser(authorId)).thenReturn(author);
        Mockito.when(itemService.checkItem(itemId)).thenReturn(item);
        Mockito.when(bookingService.existsByBookerIdAndItemIdAndEndBefore(Mockito.eq(authorId), Mockito.eq(itemId), Mockito.any()))
                .thenReturn(false);

        assertThrows(UnavailableToCommentException.class, () -> itemUtilService.comment(authorId, itemId, request));

        Mockito.verify(itemService, Mockito.never()).saveComment(Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    void findAllByOwnerId_whenItemsExist_thenReturnsEnrichedItemDtos() {
        Long ownerId = 7L;
        Item item1 = new Item(1L, "item1", "desc1", true, new User(ownerId, null, null), null);
        Item item2 = new Item(2L, "item2", "desc2", true, new User(ownerId, null, null), null);
        List<Item> items = List.of(item1, item2);

        BookingShortDto prev1 = new BookingShortDto();
        BookingShortDto next1 = new BookingShortDto();
        BookingShortDto prev2 = new BookingShortDto();
        BookingShortDto next2 = new BookingShortDto();

        Map<Long, BookingShortDto> prevsMap = Map.of(1L, prev1, 2L, prev2);
        Map<Long, BookingShortDto> nextsMap = Map.of(1L, next1, 2L, next2);
        Map<Long, List<CommentDto>> commentsMap = Map.of(
                1L, List.of(new CommentDto()),
                2L, List.of(new CommentDto())
        );

        Mockito.when(itemRepository.findAllByOwnerId(ownerId)).thenReturn(items);
        Mockito.when(itemService.findAllCommentsByItemIds(List.of(1L, 2L))).thenReturn(commentsMap);
        Mockito.when(bookingService.findAllPrevsByItemIds(List.of(1L, 2L))).thenReturn(prevsMap);
        Mockito.when(bookingService.findAllNextsByItemIds(List.of(1L, 2L))).thenReturn(nextsMap);

        Collection<ItemDto> result = itemUtilService.findAllByOwnerId(ownerId);

        assertEquals(2, result.size());
        Mockito.verify(itemRepository).findAllByOwnerId(ownerId);
        Mockito.verify(itemService).findAllCommentsByItemIds(List.of(1L, 2L));
        Mockito.verify(bookingService).findAllPrevsByItemIds(List.of(1L, 2L));
        Mockito.verify(bookingService).findAllNextsByItemIds(List.of(1L, 2L));
    }


}