package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constant.Constants;
import ru.practicum.shareit.request.dto.NewItemRequestDto;

/**
 * TODO Sprint add-item-requests.
 */
@Slf4j
@Validated
@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@Valid @RequestBody NewItemRequestDto newItemRequestDto,
                                                    @RequestHeader(Constants.X_SHARER_USER_ID)
                                                    @Min(value = 1) Long userId) {
        log.info("Полученное тело запроса на создания запроса вещи: {}", newItemRequestDto.toString());
        return itemRequestClient.createItemRequest(newItemRequestDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllItemRequestsForRequestor(@RequestHeader(Constants.X_SHARER_USER_ID)
                                                                  @Min(value = 1) Long userId) {
        log.info("Запрос на получение своих запросов вещей пользователем (id = {})", userId);
        return itemRequestClient.getAllItemRequestsForRequestor(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAllItemRequests(@RequestHeader(Constants.X_SHARER_USER_ID)
                                                      @Min(value = 1) Long userId) {
        log.info("Запрос на получение всех запросов вещей пользователем (id = {})", userId);
        return itemRequestClient.getAllItemRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findItemRequest(@PathVariable("requestId")
                                                  @Min(value = 1) Long requestId,
                                                  @RequestHeader(Constants.X_SHARER_USER_ID)
                                                  @Min(value = 1) Long userId) {
        log.info("Запрос на получение запроса вещи (id = {}) пользователем (id = {})", requestId, userId);
        return itemRequestClient.getItemRequest(requestId, userId);
    }
}
