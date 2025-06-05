package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;


@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto create(@RequestBody NewUserRequest newUserRequest) {
        return userService.save(newUserRequest);
    }

    @GetMapping
    public Collection<UserDto> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{userId}")
    public UserDto findUserById(@PathVariable Long userId) {
        return userService.findUserById(userId);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable("userId") Long userId,
                          @RequestBody UpdateUserRequest newUserRequest) {
        return userService.update(userId, newUserRequest);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long userId) {
        userService.delete(userId);
    }
}
