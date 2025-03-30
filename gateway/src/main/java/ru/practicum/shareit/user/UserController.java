package ru.practicum.shareit.user;

import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@Validated
@Controller
@RequestMapping(path = "/users")
public class UserController {
    final UserClient userClient;

    @Autowired
    public UserController(UserClient userClient) {
        this.userClient = userClient;
    }

    // Создание пользователя
    @PostMapping
    public ResponseEntity<Object> createUser(@Validated @RequestBody NewUserRequest newUserRequest) {
        log.info("Полученное тело запроса на создание пользователя: {}", newUserRequest.toString());
        return userClient.createUser(newUserRequest);
    }

    // Получение всех пользователей
    @GetMapping
    public ResponseEntity<Object> findAllUsers() {
        log.info("Запрос на получение всех пользователей");
        return userClient.getAllUsers();
    }

    // Получение пользователя по id
    @GetMapping("/{userId}")
    public ResponseEntity<Object> findUser(@PathVariable("userId") @Min(value = 1) Long userId) {
        log.info("Запрос на получение пользователя по id = {}", userId);
        return userClient.getUser(userId);
    }

    // Обновление пользователя
    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@Validated @RequestBody UpdateUserRequest updateUserRequest,
                                             @PathVariable("userId") @Min(value = 1) Long userId) {
        log.info("Полученное тело запроса на обновление пользователя: {}", updateUserRequest.toString());
        return userClient.updateUser(updateUserRequest, userId);
    }

    // Удаление пользователя
    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> removeUser(@PathVariable("userId") @Min(value = 1) Long userId) {
        log.info("Запрос на удаление пользователя");
        return userClient.removeUser(userId);
    }
}
