package ru.practicum.shareit.request.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDto {
    Long itemId;
    String name;
    Long ownerId;
}
