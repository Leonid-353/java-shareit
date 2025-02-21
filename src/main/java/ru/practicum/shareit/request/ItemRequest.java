package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequest {
    Long id;
    String description; // текст запроса, содержащий описание требуемой вещи
    User requestor; // пользователь, создавший запрос
    LocalDateTime created; // дата и время создания запроса
}
