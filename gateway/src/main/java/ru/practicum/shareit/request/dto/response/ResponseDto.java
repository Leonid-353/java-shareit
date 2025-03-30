package ru.practicum.shareit.request.dto.response;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDto {
    Long itemId;
    String name;
    Long ownerId;
}
