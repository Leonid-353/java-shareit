package ru.practicum.shareit.booking.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {
    public static Booking mapToBooking(NewBookingRequest request, User user, Item item) {

        Booking booking = new Booking();

        booking.setItem(item);
        booking.setStart(request.getStart());
        booking.setEnd(request.getEnd());
        booking.setBooker(user);
        booking.setStatus(request.getStatus());

        return booking;
    }

    public static BookingDto mapToBookingDto(Booking booking) {
        DateTimeFormatter formatter = DateTimeFormatter
                .ISO_LOCAL_DATE_TIME
                .withZone(ZoneOffset.UTC);

        BookingDto dto = new BookingDto();

        dto.setId(booking.getId());
        dto.setStart(formatter.format(booking.getStart()));
        dto.setEnd(formatter.format(booking.getEnd()));
        dto.setItem(ItemMapper.mapToItemDto(booking.getItem()));
        dto.setBooker(UserMapper.mapToUserDto(booking.getBooker()));
        dto.setStatus(booking.getStatus());

        return dto;
    }
}
