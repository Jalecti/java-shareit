package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictEmailException;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final InMemoryUserRepository userRepository;

    @Override
    public UserDto save(NewUserRequest newUserRequest) {
        checkUserEmail(newUserRequest.getEmail());
        User user = UserMapper.mapToUser(newUserRequest);
        user = userRepository.save(user);
        log.info("Пользователь {} зарегистрирован с ID: {}", newUserRequest.getEmail(), user.getId());
        return UserMapper.mapToUserDto(user);
    }

    @Override
    public Collection<UserDto> findAll() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto findUserById(Long userId) {
        return UserMapper.mapToUserDto(checkUser(userId));
    }

    @Override
    public UserDto update(Long userId, UpdateUserRequest updateUserRequest) {
        checkUserEmail(updateUserRequest.getEmail());
        updateUserRequest.setId(userId);
        User updatedUser = UserMapper.updateUserFields(checkUser(userId), updateUserRequest);
        updatedUser = userRepository.update(updatedUser);
        log.info("Пользователь обновлен с ID: {}", userId);
        return UserMapper.mapToUserDto(updatedUser);
    }

    @Override
    public void delete(Long userId) {
        User userToDelete = checkUser(userId);
        String email = userToDelete.getEmail();
        userRepository.delete(userId);
        log.info("Пользователь {} с ID: {} удален", email, userId);
    }

    public User checkUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> {
            log.error("Пользователь не найден с ID: {}", userId);
            return new NotFoundException("Пользователь не найден с ID: " + userId);
        });
    }

    public void checkUserEmail(String userEmail) {
        if (userRepository.findByEmail(userEmail).isPresent()) {
            log.error("Пользователь с указанным email уже зарегистрирован: {}", userEmail);
            throw new ConflictEmailException("Пользователь с указанным email уже зарегистрирован: " + userEmail);
        }
    }
}
