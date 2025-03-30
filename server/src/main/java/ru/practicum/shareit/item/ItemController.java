package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constant.Constants;
import ru.practicum.shareit.item.dto.ItemCommentsDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.dto.comment.NewCommentRequest;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {
    final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    // Создание вещи
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto createItem(@RequestBody NewItemRequest newItemRequest,
                              @RequestHeader(Constants.X_SHARER_USER_ID) Long ownerId) {
        log.info("Полученное тело запроса на создание вещи: {}", newItemRequest.toString());
        return itemService.createItem(newItemRequest, ownerId);
    }

    // Получение всех вещей владельца
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemCommentsDto> findAllOwnerItems(@RequestHeader(Constants.X_SHARER_USER_ID) Long ownerId) {
        log.info("Запрос на получение владельцем (id = {}) всех его вещей", ownerId);
        return itemService.findAllOwnerItems(ownerId);
    }

    // Получение вещи по id
    @GetMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemCommentsDto findItem(@PathVariable("itemId") Long itemId,
                                    @RequestHeader(Constants.X_SHARER_USER_ID) Long userId) {
        log.info("Запрос на получение вещи по id = {}", itemId);
        return itemService.findItem(itemId, userId);
    }

    // Обновление вещи владельцем
    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto updateItem(@RequestBody UpdateItemRequest updateItemRequest,
                              @PathVariable("itemId") Long itemId,
                              @RequestHeader(Constants.X_SHARER_USER_ID) Long ownerId) {
        log.info("Запрос на обновление вещи (id = {}) владельцем (id = {})", itemId, ownerId);
        return itemService.updateItem(updateItemRequest, itemId, ownerId);
    }

    // Удаление вещи владельцем
    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeItem(@PathVariable("itemId") Long itemId,
                           @RequestHeader(Constants.X_SHARER_USER_ID) Long ownerId) {
        log.info("Запрос на удаление вещи (id = {}) владельцем (id = {})", itemId, ownerId);
        itemService.removeItem(itemId, ownerId);
    }

    // Поиск вещи потенциальным арендатором по имени или описанию
    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> searchItemByNameOrDescription(@RequestParam String text,
                                                       @RequestHeader(Constants.X_SHARER_USER_ID) Long userId) {
        log.info("Запрос на поиск вещей пользователем (id = {})", userId);
        return itemService.searchItemByNameOrDescription(text, userId);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createdComment(@RequestBody NewCommentRequest newCommentRequest,
                                     @PathVariable("itemId") Long itemId,
                                     @RequestHeader(Constants.X_SHARER_USER_ID) Long bookerId) {
        log.info("Полученное тело запроса на создание комментария: {}", newCommentRequest.toString());
        return itemService.createComment(newCommentRequest, itemId, bookerId);
    }
}
