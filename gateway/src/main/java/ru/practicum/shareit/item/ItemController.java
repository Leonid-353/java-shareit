package ru.practicum.shareit.item;

import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constant.Constants;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.dto.comment.NewCommentRequest;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@Validated
@Controller
@RequestMapping("/items")
public class ItemController {
    final ItemClient itemClient;

    @Autowired
    public ItemController(ItemClient itemClient) {
        this.itemClient = itemClient;
    }

    // Создание вещи
    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(Constants.X_SHARER_USER_ID) @Min(value = 1) Long ownerId,
                                             @Validated @RequestBody NewItemRequest newItemRequest) {
        log.info("Полученное тело запроса на создание вещи: {}", newItemRequest.toString());
        return itemClient.createItem(ownerId, newItemRequest);
    }

    // Получение всех вещей владельца
    @GetMapping
    public ResponseEntity<Object> findAllOwnerItems(@RequestHeader(Constants.X_SHARER_USER_ID) @Min(value = 1) Long ownerId) {
        log.info("Запрос на получение владельцем (id = {}) всех его вещей", ownerId);
        return itemClient.getAllOwnerItems(ownerId);
    }

    // Получение вещи по id
    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findItem(@PathVariable("itemId") @Min(value = 1) Long itemId,
                                           @RequestHeader(Constants.X_SHARER_USER_ID) @Min(value = 1) Long userId) {
        log.info("Запрос на получение вещи по id = {}", itemId);
        return itemClient.getItem(itemId, userId);
    }

    // Обновление вещи владельцем
    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@Validated @RequestBody UpdateItemRequest updateItemRequest,
                                             @PathVariable("itemId") @Min(value = 1) Long itemId,
                                             @RequestHeader(Constants.X_SHARER_USER_ID) @Min(value = 1) Long ownerId) {
        log.info("Запрос на обновление вещи (id = {}) владельцем (id = {})", itemId, ownerId);
        return itemClient.updateItem(updateItemRequest, itemId, ownerId);
    }

    // Удаление вещи владельцем
    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> removeItem(@PathVariable("itemId") @Min(value = 1) Long itemId,
                                             @RequestHeader(Constants.X_SHARER_USER_ID) @Min(value = 1) Long ownerId) {
        log.info("Запрос на удаление вещи (id = {}) владельцем (id = {})", itemId, ownerId);
        return itemClient.removeItem(itemId, ownerId);
    }

    // Поиск вещи потенциальным арендатором по имени или описанию
    @GetMapping("/search")
    public ResponseEntity<Object> searchItemByNameOrDescription(@RequestParam String text,
                                                                @RequestHeader(Constants.X_SHARER_USER_ID)
                                                                @Min(value = 1) Long userId) {
        log.info("Запрос на поиск вещей пользователем (id = {})", userId);
        return itemClient.searchItemByNameOrDescription(text, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createdComment(@Validated @RequestBody NewCommentRequest newCommentRequest,
                                                 @PathVariable("itemId") @Min(value = 1) Long itemId,
                                                 @RequestHeader(Constants.X_SHARER_USER_ID) @Min(value = 1) Long bookerId) {
        log.info("Полученное тело запроса на создание комментария: {}", newCommentRequest.toString());
        return itemClient.createComment(newCommentRequest, itemId, bookerId);
    }
}
