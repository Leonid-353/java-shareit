package ru.practicum.shareit.request.dto.response;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequestWithResponsesDto {
    Long id;
    String description;
    UserDto requestor;
    LocalDateTime created;
    List<ResponseDto> items;
}
