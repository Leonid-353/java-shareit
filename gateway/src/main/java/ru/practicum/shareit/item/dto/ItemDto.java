package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.BookingDates;
import ru.practicum.shareit.user.dto.UserDto;

/**
 * TODO Sprint add-controllers.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDto {
    Long id;
    @NotBlank
    String name;
    @NotBlank
    String description;
    UserDto owner;
    boolean available;
    BookingDates lastBooking;
    BookingDates nextBooking;
    Long request;
}
