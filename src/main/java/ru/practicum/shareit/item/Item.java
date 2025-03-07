package ru.practicum.shareit.item;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

/**
 * TODO Sprint add-controllers.
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Item {
    Long id;
    @NotBlank(message = "Название не может быть пустым")
    String name;
    @Size(min = 1, max = 200, message = "Максимальная длина описания - 200 символов")
    String description;
    boolean available;
    @NotNull
    User owner; // владелец вещи
    @NotNull
    ItemRequest request; // ссылка на соответствующий запрос (если вещь была создана по запросу другого пользователя)
}
