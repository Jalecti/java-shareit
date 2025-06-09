package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentStorage;
import ru.practicum.shareit.item.comment.NewCommentRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ItemServiceImplIntegrationTest {
    @Autowired
    private ItemService itemService;

    @Autowired
    private UserStorage userRepository;

    @Autowired
    private ItemStorage itemRepository;

    @Autowired
    private CommentStorage commentRepository;

    private User user;

    @BeforeEach
    void beforeEach() {
        user = new User(null, "user", "user@email.com");
        user = userRepository.save(user);
    }

    @Test
    void save_whenValidItem_thenReturnsSavedItemDto() {
        NewItemRequest newItem = new NewItemRequest();
        newItem.setName("item");
        newItem.setDescription("description");
        newItem.setAvailable(true);

        ItemDto savedItem = itemService.save(user.getId(), newItem, null);
        ItemDto foundItem = itemService.findItemById(savedItem.getId());

        assertEquals(savedItem.getId(), foundItem.getId());
        assertEquals("item", foundItem.getName());
        assertTrue(foundItem.getAvailable());
    }

    @Test
    void update_whenValidUpdateRequest_thenChangesItemFields() {
        NewItemRequest newItem = new NewItemRequest("name", "desc", true, null);
        ItemDto savedItem = itemService.save(user.getId(), newItem, null);

        UpdateItemRequest update = new UpdateItemRequest();
        update.setName("uname");
        update.setAvailable(false);

        ItemDto updated = itemService.update(user.getId(), savedItem.getId(), update);

        assertEquals("uname", updated.getName());
        assertFalse(updated.getAvailable());
    }

    @Test
    void saveComment_whenValidRequest_thenReturnsSavedCommentDto() {
        NewItemRequest newItem = new NewItemRequest("item", "desc", true, null);
        ItemDto item = itemService.save(user.getId(), newItem, null);
        Item itemEntity = itemRepository.findById(item.getId()).orElseThrow();

        NewCommentRequest commentRequest = new NewCommentRequest("comment");
        CommentDto comment = itemService.saveComment(user, itemEntity, commentRequest);

        assertEquals("comment", comment.getText());
        assertEquals(item.getId(), comment.getItemId());
    }

    @Test
    void delete_whenItemDeleted_thenShouldThrowNotFoundOnSearch() {
        NewItemRequest newItem = new NewItemRequest("name", "desc", true, null);
        ItemDto item = itemService.save(user.getId(), newItem, null);

        itemService.delete(user.getId(), item.getId());

        assertThrows(NotFoundException.class, () -> itemService.findItemById(item.getId()));
    }


}