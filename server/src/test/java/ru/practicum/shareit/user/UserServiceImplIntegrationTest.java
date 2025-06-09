package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictEmailException;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserServiceImplIntegrationTest {
    @Autowired
    private UserService userService;

    @Autowired
    private UserStorage userRepository;

    @BeforeEach
    void beforeEach() {
        userRepository.deleteAll();
    }

    @Test
    void save_whenValidRequest_thenReturnsItemRequestDto() {
        NewUserRequest request = new NewUserRequest("name", "name@mail.com");
        UserDto saved = userService.save(request);

        UserDto found = userService.findUserById(saved.getId());

        assertEquals("name", found.getName());
        assertEquals("name@mail.com", found.getEmail());
    }

    @Test
    void findAll_whenUsersExist_thenReturnsAllUsers() {
        userService.save(new NewUserRequest("user1", "user1@mail.com"));
        userService.save(new NewUserRequest("user2", "user2@mail.com"));

        Collection<UserDto> users = userService.findAll();

        assertEquals(2, users.size());
    }

    @Test
    void update_whenValidUpdateRequest_thenUpdatesUserFields() {
        UserDto saved = userService.save(new NewUserRequest("old", "old@mail.com"));
        UpdateUserRequest update = new UpdateUserRequest(1L,"new", "new@mail.com");

        UserDto updated = userService.update(saved.getId(), update);

        assertEquals("new", updated.getName());
        assertEquals("new@mail.com", updated.getEmail());
    }

    @Test
    void delete_whenUserDeleted_thenNotFoundOnSearch() {
        UserDto user = userService.save(new NewUserRequest("name", "name@mail.com"));

        userService.delete(user.getId());

        assertThrows(NotFoundException.class, () -> userService.findUserById(user.getId()));
    }

    @Test
    void save_whenDuplicateEmail_thenThrowsConflictEmailException() {
        userService.save(new NewUserRequest("name1", "name@mail.com"));

        assertThrows(ConflictEmailException.class, () ->
                userService.save(new NewUserRequest("name2", "name@mail.com")));
    }

}