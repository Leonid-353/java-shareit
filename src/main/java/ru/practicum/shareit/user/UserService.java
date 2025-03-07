package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.Comparator;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserService {
    final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDto createUser(NewUserRequest newUserRequest) {
        User user = UserMapper.mapToUser(newUserRequest);
        userRepository.createUser(user);
        return UserMapper.mapToUserDto(user);
    }

    public Collection<UserDto> findAllUsers() {
        return userRepository.findAllUsers().stream()
                .map(UserMapper::mapToUserDto)
                .sorted(Comparator.comparing(UserDto::getId))
                .toList();
    }

    public UserDto findUser(Long userId) {
        return userRepository.findUser(userId)
                .map(UserMapper::mapToUserDto)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %d не найден", userId)));
    }

    public UserDto updateUser(UpdateUserRequest updateUserRequest,
                              Long userId) {
        User updatedUser = userRepository.updateUser(updateUserRequest, userId);
        return UserMapper.mapToUserDto(updatedUser);
    }

    public void removeUser(Long userId) {
        userRepository.removeUser(userId);
    }
}
