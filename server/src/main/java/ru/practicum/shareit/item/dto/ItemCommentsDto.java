package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.model.BookingDates;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemCommentsDto {
    Long id;
    String name;
    String description;
    UserDto owner;
    boolean available;
    BookingDates lastBooking;
    BookingDates nextBooking;
    Set<CommentDto> comments;
    Long request;
}
