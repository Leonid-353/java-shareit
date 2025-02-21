package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateItemRequest {
    String name;
    @Size(min = 1, max = 200, message = "Максимальная длина описания - 200 символов")
    String description;
    Boolean available;

    public boolean hasName() {
        return !(name == null || name.isBlank());
    }

    public boolean hasDescription() {
        return !(description == null || description.isBlank());
    }

    public boolean hasAvailable() {
        return available != null;
    }
}
