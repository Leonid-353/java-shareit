package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NewItemRequest {
    @NotBlank(message = "Название не может быть пустым")
    String name;
    @Size(min = 1, max = 200, message = "Максимальная длина описания - 200 символов")
    @NotBlank(message = "Описание не может быть пустым")
    String description;
    @NotNull
    Boolean available;
    Long owner; // владелец вещи
    Long request; // ссылка на соответствующий запрос (если вещь была создана по запросу другого пользователя)
}
