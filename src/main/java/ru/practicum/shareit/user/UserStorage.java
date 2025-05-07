package ru.practicum.shareit.user;


import java.util.Collection;
import java.util.Optional;

public interface UserStorage {

    Collection<User> findAll();

    User save(User user);

    User update(User newUser);

    void delete(Long userId);

    Optional<User> findById(Long userId);

    Optional<User> findByEmail(String userEmail);
}
