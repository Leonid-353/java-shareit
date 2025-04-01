package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class NewItemRequestDto {
    @NotBlank(message = "Описание в запросе не может быть пустым")
    @Size(min = 1, max = 512, message = "Описание в запросе не более 512 символов")
    String description;
    LocalDateTime created = LocalDateTime.now();
}
