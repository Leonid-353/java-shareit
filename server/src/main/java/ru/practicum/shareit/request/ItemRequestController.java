package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constant.Constants;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.dto.response.ItemRequestWithResponsesDto;

import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    final ItemRequestService itemRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestDto createItemRequest(@RequestBody NewItemRequestDto newItemRequestDto,
                                            @RequestHeader(Constants.X_SHARER_USER_ID) Long userId) {
        log.info("Полученное тело запроса на создания запроса вещи: {}", newItemRequestDto.toString());
        return itemRequestService.createItemRequest(newItemRequestDto, userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemRequestWithResponsesDto> findAllItemRequestsForRequestor(
            @RequestHeader(Constants.X_SHARER_USER_ID) Long userId) {
        log.info("Запрос на получение своих запросов вещей пользователем (id = {})", userId);
        return itemRequestService.findAllItemRequestsForRequestor(userId);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemRequestDto> findAllItemRequests(@RequestHeader(Constants.X_SHARER_USER_ID)
                                                    Long userId) {
        log.info("Запрос на получение всех запросов вещей пользователем (id = {})", userId);
        return itemRequestService.findAllItemRequests(userId);
    }

    @GetMapping("/{requestId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemRequestWithResponsesDto findItemRequest(@PathVariable("requestId") Long requestId,
                                                       @RequestHeader(Constants.X_SHARER_USER_ID) Long userId) {
        log.info("Запрос на получение запроса вещи (id = {}) пользователем (id = {})", requestId, userId);
        return itemRequestService.findItemRequest(requestId, userId);
    }
}
