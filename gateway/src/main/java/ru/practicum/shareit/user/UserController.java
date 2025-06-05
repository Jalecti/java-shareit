package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;


@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody NewUserRequest newUserRequest) {
        log.info("Creating user {}", newUserRequest);
        return userClient.create(newUserRequest);
    }

    @GetMapping
    public ResponseEntity<Object> findAll() {
        log.info("Get all users");
        return userClient.findAll();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> findUserById(@PathVariable Long userId) {
        log.info("Get user by id={}", userId);
        return userClient.findUserById(userId);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@PathVariable("userId") Long userId,
                          @Valid @RequestBody UpdateUserRequest newUserRequest) {
        log.info("Updating user id={} body={}", userId, newUserRequest);
        return userClient.update(userId, newUserRequest);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Object> delete(@PathVariable Long userId) {
        log.info("Delete user id={}", userId);
        return userClient.delete(userId);
    }
}
