package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.ItemUnavailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentStorage;
import ru.practicum.shareit.item.comment.NewCommentRequest;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplUnitTest {

    @Mock
    private ItemStorage itemRepository;
    @Mock
    private UserService userService;
    @Mock
    private CommentStorage commentRepository;

    @InjectMocks
    private ItemServiceImpl itemService;


    @Test
    void save_whenValidInput_thenReturnsCorrectItemDto() {
        Long userId = 1L;
        NewItemRequest newItemRequest = new NewItemRequest("name", "desc", true, null);
        ItemRequest itemRequest = null;
        User owner = new User(userId, "name", "email@email.com");
        Item item = new Item(null, "name", "desc", true, owner, null);

        Mockito.when(userService.checkUser(userId)).thenReturn(owner);
        Mockito.when(itemRepository.save(Mockito.any())).thenReturn(item);

        ItemDto result = itemService.save(userId, newItemRequest, itemRequest);

        assertEquals("name", result.getName());
        assertEquals("desc", result.getDescription());
        assertEquals(true, result.getAvailable());
        assertEquals(owner.getEmail(), result.getOwner().getEmail());
        assertNull(result.getRequest());

        ArgumentCaptor<Item> captor = ArgumentCaptor.forClass(Item.class);
        Mockito.verify(itemRepository).save(captor.capture());

        Item arg1 = captor.getValue();
        assertEquals("name", arg1.getName());
        assertEquals("desc", arg1.getDescription());
    }

    @Test
    void findItemById_whenValidInput_thenReturnsCorrectItemDto() {
        Long itemId = 36L;
        Item item = new Item(itemId, "name", "desc", true, new User(), null);

        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        ItemDto result = itemService.findItemById(itemId);

        assertEquals("name", result.getName());
        assertEquals("desc", result.getDescription());
        assertEquals(true, result.getAvailable());

        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
        Mockito.verify(itemRepository).findById(captor.capture());

        Long arg1 = captor.getValue();
        assertEquals(itemId, arg1);
    }

    @Test
    void findAllByText_whenItemsFound_thenReturnsItemDtosWithComments() {
        String searchText = "text";
        Item item1 = new Item(1L, "text1", "desc1", true, new User(), null);
        Item item2 = new Item(2L, "text2", "desc2", true, new User(), null);
        List<Item> items = List.of(item1, item2);

        Comment comment1 = new Comment(1L, "good", item1, new User(), LocalDateTime.now());
        Comment comment2 = new Comment(2L, "bad", item2, new User(), LocalDateTime.now());

        Mockito.when(itemRepository.findAllByText(searchText)).thenReturn(items);
        Mockito.when(commentRepository.findByItemIdIn(List.of(1L, 2L)))
                .thenReturn(List.of(comment1, comment2));

        Collection<ItemDto> result = itemService.findAllByText(searchText);

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(dto -> dto.getName().equals("text1")));
        assertTrue(result.stream().anyMatch(dto -> dto.getName().equals("text2")));

        Mockito.verify(itemRepository).findAllByText(searchText);
        Mockito.verify(commentRepository).findByItemIdIn(List.of(1L, 2L));
    }


    @Test
    void update_whenOwnerMatches_thenItemIsUpdated() {
        Long userId = 1L;
        Long itemId = 100L;
        User owner = new User(userId, "name", "email@email.com");
        Item originalItem = new Item(itemId, "oldName", "oldDesc", true, owner, null);
        UpdateItemRequest updateRequest = new UpdateItemRequest();
        updateRequest.setName("newName");
        updateRequest.setDescription("newDesc");
        updateRequest.setAvailable(false);

        Item updatedItem = new Item(itemId, "newName", "newDesc", false, owner, null);

        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.of(originalItem));
        Mockito.when(itemRepository.save(Mockito.any())).thenReturn(updatedItem);
        Mockito.when(commentRepository.findByItemId(itemId)).thenReturn(List.of());

        ItemDto result = itemService.update(userId, itemId, updateRequest);

        assertEquals("newName", result.getName());
        assertEquals("newDesc", result.getDescription());
        assertFalse(result.getAvailable());

        Mockito.verify(itemRepository).save(Mockito.any());
    }

    @Test
    void update_whenUserIsNotOwner_thenThrowsForbiddenException() {
        Long userId = 1L;
        Long itemId = 100L;
        User anotherUser = new User(2L, "other", "other@email.com");
        Item item = new Item(itemId, "name", "desc", true, anotherUser, null);

        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        UpdateItemRequest request = new UpdateItemRequest();

        assertThrows(ForbiddenException.class, () ->
                itemService.update(userId, itemId, request));
    }

    @Test
    void delete_whenOwnerMatches_thenItemIsDeleted() {
        Long userId = 1L;
        Long itemId = 10L;
        User owner = new User(userId, "owner", "owner@email.com");
        Item item = new Item(itemId, "item", "desc", true, owner, null);

        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        itemService.delete(userId, itemId);

        Mockito.verify(itemRepository).deleteById(itemId);
    }

    @Test
    void delete_whenUserIsNotOwner_thenThrowsForbiddenException() {
        Long userId = 1L;
        Long itemId = 10L;
        User otherUser = new User(2L, "Other", "other@email.com");
        Item item = new Item(itemId, "item", "desc", true, otherUser, null);

        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(ForbiddenException.class, () ->
                itemService.delete(userId, itemId));

        Mockito.verify(itemRepository, Mockito.never()).deleteById(Mockito.any());
    }

    @Test
    void checkItem_whenItemExists_thenReturnsItem() {
        Long itemId = 99L;
        Item item = new Item(itemId, "item", "desc", true, new User(), null);

        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        Item result = itemService.checkItem(itemId);

        assertEquals(itemId, result.getId());
        Mockito.verify(itemRepository).findById(itemId);
    }

    @Test
    void checkItem_whenItemNotFound_thenThrowsNotFoundException() {
        Long itemId = 99L;

        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.checkItem(itemId));

        Mockito.verify(itemRepository).findById(itemId);
    }

    @Test
    void checkAvailableItem_whenItemAvailable_thenReturnsItem() {
        Long itemId = 123L;
        Item item = new Item(itemId, "item", "desc", true, new User(), null);

        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        Item result = itemService.checkAvailableItem(itemId);

        assertEquals(itemId, result.getId());
        Mockito.verify(itemRepository).findById(itemId);
    }

    @Test
    void checkAvailableItem_whenItemUnavailable_thenThrowsException() {
        Long itemId = 123L;
        Item item = new Item(itemId, "item", "desc", false, new User(), null);

        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(ItemUnavailableException.class, () ->
                itemService.checkAvailableItem(itemId));

        Mockito.verify(itemRepository).findById(itemId);
    }

    @Test
    void checkOwner_whenUserIsOwner_thenDoesNothing() {
        Long userId = 1L;
        User owner = new User(userId, "owner", "owner@email.com");
        Item item = new Item(1L, "item", "desc", true, owner, null);

        assertDoesNotThrow(() -> itemService.checkOwner(userId, item));
    }

    @Test
    void checkOwner_whenUserIsNotOwner_thenThrowsForbiddenException() {
        Long userId = 1L;
        User owner = new User(2L, "other", "other@email.com");
        Item item = new Item(1L, "item", "desc", true, owner, null);

        assertThrows(ForbiddenException.class, () -> itemService.checkOwner(userId, item));
    }

    @Test
    void saveComment_whenValidInput_thenReturnsCommentDto() {
        Long itemId = 10L;
        Long authorId = 5L;
        User author = new User(authorId, "author", "author@email.com");
        Item item = new Item(itemId, "item", "desc", true, new User(), null);
        NewCommentRequest request = new NewCommentRequest("comment");

        Comment saved = new Comment(1L, request.getText(), item, author, LocalDateTime.now());

        Mockito.when(commentRepository.save(Mockito.any())).thenReturn(saved);

        CommentDto result = itemService.saveComment(author, item, request);

        assertEquals(request.getText(), result.getText());
        assertEquals(itemId, result.getItemId());
        assertEquals(author.getName(), result.getAuthorName());

        Mockito.verify(commentRepository).save(Mockito.any());
    }

    @Test
    void findAllByRequestId_whenItemsExist_thenReturnsShortDtos() {
        Long requestId = 5L;
        Item item1 = new Item(101L, "item1", "desc1", true, new User(), new ItemRequest(requestId, null, null, null));
        Item item2 = new Item(102L, "item2", "desc2", true, new User(), new ItemRequest(requestId, null, null, null));

        Mockito.when(itemRepository.findAllByRequestId(requestId))
                .thenReturn(List.of(item1, item2));

        Collection<ItemShortDto> result = itemService.findAllByRequestId(requestId);

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(i -> i.getId().equals(101L)));
        assertTrue(result.stream().anyMatch(i -> i.getId().equals(102L)));

        Mockito.verify(itemRepository).findAllByRequestId(requestId);
    }
}