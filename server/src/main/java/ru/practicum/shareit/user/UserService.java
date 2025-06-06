package ru.practicum.shareit.user;

import java.util.Collection;


public interface UserService {

    UserDto save(NewUserRequest newUserRequest);

    Collection<UserDto> findAll();

    UserDto findUserById(Long userId);

    UserDto update(Long userId, UpdateUserRequest updateUserRequest);

    void delete(Long userId);

    User checkUser(Long userId);

}
