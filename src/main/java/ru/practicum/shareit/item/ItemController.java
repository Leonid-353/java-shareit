package ru.practicum.shareit.item;

import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

import java.util.Collection;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@Validated
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
    public ItemDto createItem(@Validated @RequestBody NewItemRequest newItemRequest,
                              @RequestHeader("X-Sharer-User-Id") @Min(value = 1) Long ownerId) {
        log.info("Полученное тело запроса: {}", newItemRequest.toString());
        return itemService.createItem(newItemRequest, ownerId);
    }

    // Получение всех вещей владельца
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<ItemDto> findAllOwnerItems(@RequestHeader("X-Sharer-User-Id") @Min(value = 1) Long ownerId) {
        return itemService.findAllOwnerItems(ownerId);
    }

    // Получение вещи по id
    @GetMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto findItem(@PathVariable("itemId") @Min(value = 1) Long itemId) {
        return itemService.findItem(itemId);
    }

    // Обновление вещи владельцем
    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto updateItem(@Validated @RequestBody UpdateItemRequest updateItemRequest,
                              @PathVariable("itemId") @Min(value = 1) Long itemId,
                              @RequestHeader("X-Sharer-User-Id") @Min(value = 1) Long ownerId) {
        return itemService.updateItem(updateItemRequest, itemId, ownerId);
    }

    // Удаление вещи владельцем
    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeItem(@PathVariable("itemId") @Min(value = 1) Long itemId,
                           @RequestHeader("X-Sharer-User-Id") @Min(value = 1) Long ownerId) {
        itemService.removeItem(itemId, ownerId);
    }

    // Поиск вещи потенциальным арендатором по имени или описанию
    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public Collection<ItemDto> searchItemByNameOrDescription(@RequestParam String text,
                                                             @RequestHeader("X-Sharer-User-Id")
                                                             @Min(value = 1) Long userId) {
        return itemService.searchItemByNameOrDescription(text, userId);
    }
}
