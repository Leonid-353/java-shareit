package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Repository
public class InMemoryUserRepository {

    Map<Long, User> users = new HashMap<>();

    public User createUser(User user) {
        for (Map.Entry<Long, User> entry : users.entrySet()) {
            if (entry.getValue().getEmail().equals(user.getEmail())) {
                throw new DuplicatedDataException("Этот email уже используется");
            }
        }
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    public Collection<User> findAllUsers() {
        return users.values();
    }

    public Optional<User> findUser(Long userId) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException(String.format("Пользователь с id = %d не найден", userId));
        }
        return Optional.of(users.get(userId));
    }

    public User updateUser(UpdateUserRequest updateUserRequest,
                           Long userId) {
        for (Map.Entry<Long, User> entry : users.entrySet()) {
            if (entry.getValue().getEmail().equals(updateUserRequest.getEmail())) {
                throw new DuplicatedDataException("Этот email уже используется");
            }
        }
        return findUser(userId)
                .map(user -> UserMapper.updateUserFields(user, updateUserRequest))
                .orElseThrow();
    }

    public void removeUser(Long userId) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException(String.format("Пользователь с id = %d не найден", userId));
        }
        users.remove(userId);
    }

    private Long getNextId() {
        long currentMaxId = users.keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
