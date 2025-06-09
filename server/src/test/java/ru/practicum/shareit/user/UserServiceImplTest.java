package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ConflictEmailException;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserStorage userRepository;
    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void save_whenEmailIsUnique_thenSavesUser() {
        NewUserRequest request = new NewUserRequest("newUser", "new@example.com");
        User user = new User(1L, request.getName(), request.getEmail());

        Mockito.when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(user);

        UserDto result = userService.save(request);

        assertEquals(request.getName(), result.getName());
        assertEquals(request.getEmail(), result.getEmail());
        Mockito.verify(userRepository).save(Mockito.any());
    }

    @Test
    void save_whenEmailExists_thenThrowsConflictEmailException() {
        NewUserRequest request = new NewUserRequest("name", "exists@example.com");
        Mockito.when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(new User()));

        assertThrows(ConflictEmailException.class, () -> userService.save(request));
        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void findAll_whenUsersExist_thenReturnsDtos() {
        List<User> users = List.of(new User(1L, "name1", "email1@e.com"), new User(2L, "name2", "email2@e.com"));
        Mockito.when(userRepository.findAll()).thenReturn(users);

        Collection<UserDto> result = userService.findAll();

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(u -> u.getEmail().equals("email1@e.com")));
        assertTrue(result.stream().anyMatch(u -> u.getEmail().equals("email2@e.com")));
    }

    @Test
    void findUserById_whenUserExists_thenReturnsDto() {
        User user = new User(1L, "name1", "email1@e.com");
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDto result = userService.findUserById(1L);

        assertEquals(user.getEmail(), result.getEmail());
    }

    @Test
    void findUserById_whenUserNotFound_thenThrowsNotFoundException() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.findUserById(1L));
    }

    @Test
    void update_whenEmailNotChanged_thenSavesUpdatedUser() {
        Long id = 1L;
        User user = new User(id, "oldName", "same@email.com");
        UpdateUserRequest request = new UpdateUserRequest(id, "newName", "same@email.com");

        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(user));
        Mockito.when(userRepository.save(Mockito.any())).thenAnswer(inv -> inv.getArgument(0));

        UserDto result = userService.update(id, request);

        assertEquals("newName", result.getName());
        Mockito.verify(userRepository).save(Mockito.any());
    }

    @Test
    void update_whenEmailChangedToExisting_thenThrowsConflictEmailException() {
        Long id = 1L;
        User user = new User(id, "old", "old@email.com");
        UpdateUserRequest request = new UpdateUserRequest(id, "new", "new@email.com");

        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(user));
        Mockito.when(userRepository.findByEmail("new@email.com")).thenReturn(Optional.of(new User()));

        assertThrows(ConflictEmailException.class, () -> userService.update(id, request));
        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void delete_whenUserExists_thenDeletesById() {
        Long id = 1L;
        User user = new User(id, "name", "email@email.com");
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(user));

        userService.delete(id);

        Mockito.verify(userRepository).deleteById(id);
    }

    @Test
    void delete_whenUserNotFound_thenThrowsNotFoundException() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.delete(1L));
        Mockito.verify(userRepository, Mockito.never()).deleteById(Mockito.any());
    }

}