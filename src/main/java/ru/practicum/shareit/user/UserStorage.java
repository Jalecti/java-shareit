package ru.practicum.shareit.user;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserStorage extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String userEmail);
}
