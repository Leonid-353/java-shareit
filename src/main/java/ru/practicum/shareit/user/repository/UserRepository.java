package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UpdateUserRequest;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository {
    User createUser(User user);

    Collection<User> findAllUsers();

    Optional<User> findUser(Long userId);

    User updateUser(UpdateUserRequest updateUserRequest, Long userId);

    void removeUser(Long userId);
}
