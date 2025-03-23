package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@Transactional(readOnly = true)
public class UserService {
    final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public UserDto createUser(NewUserRequest newUserRequest) {
        User user = userRepository.save(UserMapper.mapToUser(newUserRequest));
        log.info("Пользователь добавлен в базу данных, id = {}", user.getId());
        return UserMapper.mapToUserDto(user);
    }

    public Collection<UserDto> findAllUsers() {
        return userRepository.findAll(Sort.by("id").ascending()).stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    public UserDto findUser(Long userId) {
        return userRepository.findById(userId)
                .map(UserMapper::mapToUserDto)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %d не найден", userId)));
    }

    @Transactional
    public UserDto updateUser(UpdateUserRequest updateUserRequest,
                              Long userId) {
        User updatedUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        UserMapper.updateUserFields(updatedUser, updateUserRequest);

        userRepository.save(updatedUser);
        return UserMapper.mapToUserDto(updatedUser);
    }

    @Transactional
    public void removeUser(Long userId) {
        userRepository.deleteById(userId);
    }
}
