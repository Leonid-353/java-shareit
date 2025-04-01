package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewItemRequest {
    @NotBlank(message = "Название не может быть пустым")
    String name;
    @Size(min = 1, max = 200, message = "Максимальная длина описания - 200 символов")
    @NotBlank(message = "Описание не может быть пустым")
    String description;
    @NotNull
    Boolean available;
    Long requestId;
}
