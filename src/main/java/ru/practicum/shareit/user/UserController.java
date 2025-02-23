package ru.practicum.shareit.user;

import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@Validated
@RestController
@RequestMapping(path = "/users")
public class UserController {
    final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Создание пользователя
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@Validated @RequestBody NewUserRequest newUserRequest) {
        log.info("Полученное тело запроса на создание пользователя: {}", newUserRequest.toString());
        return userService.createUser(newUserRequest);
    }

    // Получение всех пользователей
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<UserDto> findAllUsers() {
        log.info("Запрос на получение всех пользователей");
        return userService.findAllUsers();
    }

    // Получение пользователя по id
    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto findUser(@PathVariable("userId") @Min(value = 1) Long userId) {
        log.info("Запрос на получение пользователя по id = {}", userId);
        return userService.findUser(userId);
    }

    // Обновление пользователя
    @PatchMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto updateUser(@Validated @RequestBody UpdateUserRequest updateUserRequest,
                              @PathVariable("userId") @Min(value = 1) Long userId) {
        log.info("Полученное тело запроса на обновление пользователя: {}", updateUserRequest.toString());
        return userService.updateUser(updateUserRequest, userId);
    }

    // Удаление пользователя
    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeUser(@PathVariable("userId") @Min(value = 1) Long userId) {
        log.info("Запрос на удаление пользователя");
        userService.removeUser(userId);
    }

}
