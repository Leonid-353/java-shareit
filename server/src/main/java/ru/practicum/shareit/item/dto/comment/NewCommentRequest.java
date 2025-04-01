package ru.practicum.shareit.item.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewCommentRequest {
    String text;
    LocalDateTime created;
}
