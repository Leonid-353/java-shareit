package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
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
    public UserDto createUser(@RequestBody NewUserRequest newUserRequest) {
        log.info("Полученное тело запроса на создание пользователя: {}", newUserRequest.toString());
        return userService.createUser(newUserRequest);
    }

    // Получение всех пользователей
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> findAllUsers() {
        log.info("Запрос на получение всех пользователей");
        return userService.findAllUsers();
    }

    // Получение пользователя по id
    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto findUser(@PathVariable("userId") Long userId) {
        log.info("Запрос на получение пользователя по id = {}", userId);
        return userService.findUser(userId);
    }

    // Обновление пользователя
    @PatchMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto updateUser(@RequestBody UpdateUserRequest updateUserRequest,
                              @PathVariable("userId") Long userId) {
        log.info("Полученное тело запроса на обновление пользователя: {}", updateUserRequest.toString());
        return userService.updateUser(updateUserRequest, userId);
    }

    // Удаление пользователя
    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeUser(@PathVariable("userId") Long userId) {
        log.info("Запрос на удаление пользователя");
        userService.removeUser(userId);
    }

}
