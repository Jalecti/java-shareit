package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;

import java.util.*;


@Repository
public class InMemoryUserRepository implements UserStorage {
    private final Map<Long, User> users;
    private Long counter;

    public InMemoryUserRepository() {
        counter = 0L;
        users = new HashMap<>();
    }

    @Override
    public Collection<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User save(User user) {
        user.setId(++counter);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User newUser) {
        //users.put(newUser.getId(), newUser); - для InMemory не требуется, тк обновляется в UserServiceImpl
        return newUser;
    }

    @Override
    public void delete(Long userId) {
        users.remove(userId);
    }

    @Override
    public Optional<User> findById(Long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    public Optional<User> findByEmail(String userEmail) {
        return users.values()
                .stream()
                .filter(user -> user.getEmail().equals(userEmail))
                .findFirst();
    }
}
