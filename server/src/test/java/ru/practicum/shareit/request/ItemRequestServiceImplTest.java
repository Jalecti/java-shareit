package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemShortDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {
    @Mock
    private ItemRequestStorage itemRequestRepository;
    @Mock
    private UserService userService;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Test
    void save_whenValidRequest_thenReturnsItemRequestDto() {
        Long userId = 1L;
        NewItemRequestRequest request = new NewItemRequestRequest();
        request.setDescription("request");

        User user = new User(userId, "name", "name@email.com");
        ItemRequest itemRequest = new ItemRequest(10L, request.getDescription(), user, LocalDateTime.now());

        Mockito.when(userService.checkUser(userId)).thenReturn(user);
        Mockito.when(itemRequestRepository.save(Mockito.any())).thenReturn(itemRequest);

        ItemRequestDto result = itemRequestService.save(userId, request);

        assertEquals(itemRequest.getId(), result.getId());
        assertEquals("request", result.getDescription());
        assertEquals(userId, result.getRequestor().getId());

        Mockito.verify(userService).checkUser(userId);
        Mockito.verify(itemRequestRepository).save(Mockito.any());
    }

    @Test
    void findItemRequestById_whenValid_thenReturnsDto() {
        Long requestorId = 1L;
        Long requestId = 100L;
        User user = new User(requestorId, "name", "name@email.com");
        ItemRequest itemRequest = new ItemRequest(requestId, "request", user, LocalDateTime.now());
        Collection<ItemShortDto> items = List.of();

        Mockito.when(userService.checkUser(requestorId)).thenReturn(user);
        Mockito.when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));

        ItemRequestDto result = itemRequestService.findItemRequestById(requestorId, requestId, items);

        assertEquals(requestId, result.getId());
        assertEquals("request", result.getDescription());
        assertEquals(requestorId, result.getRequestor().getId());
        Mockito.verify(userService).checkUser(requestorId);
        Mockito.verify(itemRequestRepository).findById(requestId);
    }

    @Test
    void checkItemRequest_whenNotFound_thenThrowsNotFoundException() {
        Long requestId = 404L;
        Mockito.when(itemRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> itemRequestService.checkItemRequest(requestId));
        assertTrue(ex.getMessage().contains("Запрос не найден с ID: 404"));

        Mockito.verify(itemRequestRepository).findById(requestId);
    }
}