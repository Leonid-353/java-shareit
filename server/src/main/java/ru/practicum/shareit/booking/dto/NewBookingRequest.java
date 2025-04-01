package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewBookingRequest {
    Long itemId;
    LocalDateTime start;
    LocalDateTime end;
    BookingStatus status = BookingStatus.WAITING;
}
