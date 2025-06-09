package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ItemRequestServiceImplIntegrationTest {
    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private UserStorage userRepository;

    @Autowired
    private ItemRequestStorage itemRequestRepository;

    @BeforeEach
    void beforeEach() {
        itemRequestRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void save_whenValidRequest_thenReturnsItemRequestDto() {
        User user = userRepository.save(new User(null, "name", "name@mail.com"));
        NewItemRequestRequest request = new NewItemRequestRequest("desc");

        ItemRequestDto result = itemRequestService.save(user.getId(), request);

        assertEquals("desc", result.getDescription());
        assertEquals(user.getId(), result.getRequestor().getId());
    }

    @Test
    void findItemRequestById_whenValid_thenReturnsRequest() {
        User user = userRepository.save(new User(null, "name", "name@mail.com"));
        ItemRequest itemRequest = itemRequestRepository.save(
                new ItemRequest(null, "desc", user, LocalDateTime.now()));

        ItemRequestDto dto = itemRequestService.findItemRequestById(user.getId(), itemRequest.getId(), List.of());

        assertEquals("desc", dto.getDescription());
        assertEquals(user.getId(), dto.getRequestor().getId());
        assertTrue(dto.getItems().isEmpty());
    }

    @Test
    void checkItemRequest_whenNotFound_thenThrowsException() {
        assertThrows(NotFoundException.class, () -> itemRequestService.checkItemRequest(999L));
    }
}