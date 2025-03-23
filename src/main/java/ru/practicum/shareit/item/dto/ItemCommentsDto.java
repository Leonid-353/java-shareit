package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.model.BookingDates;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemCommentsDto {
    Long id;
    @NotBlank
    String name;
    @NotBlank
    String description;
    UserDto owner;
    boolean available;
    BookingDates lastBooking;
    BookingDates nextBooking;
    Set<CommentDto> comments;
    Long request;
}
