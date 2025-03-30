package ru.practicum.shareit.booking;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.constant.Constants;

/**
 * TODO Sprint add-bookings.
 */
@Slf4j
@Validated
@Controller
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {
    final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader(Constants.X_SHARER_USER_ID) @Min(value = 1) Long userId,
                                                @Validated @RequestBody NewBookingRequest newBookingRequest) {
        log.info("Полученное тело запроса на создание бронирования: {}", newBookingRequest.toString());
        return bookingClient.bookItem(userId, newBookingRequest);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> findBooking(@RequestHeader(Constants.X_SHARER_USER_ID) @Min(value = 1) Long userId,
                                              @PathVariable("bookingId") @Min(value = 1) Long bookingId) {
        log.info("Запрос на получение бронирования (id = {}) пользователем (id = {})", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> findBookings(@RequestParam(required = false) BookingState state,
                                               @RequestHeader(Constants.X_SHARER_USER_ID)
                                               @Min(value = 1) Long userId) {
        log.info("Запрос на получение всех бронирований пользователя (id = {})", userId);
        return bookingClient.getBookingsByUserId(userId, state);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findBookingsForOwner(@RequestParam(required = false) BookingState state,
                                                       @RequestHeader(Constants.X_SHARER_USER_ID)
                                                       @Min(value = 1) Long ownerId) {
        log.info("Запрос на получение всех бронирований вещей владельца (id = {})", ownerId);
        return bookingClient.getBookingsForOwner(state, ownerId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approvedBooking(@PathVariable("bookingId") @Min(value = 1) Long bookingId,
                                                  @RequestParam Boolean approved,
                                                  @RequestHeader(Constants.X_SHARER_USER_ID)
                                                  @Min(value = 1) Long ownerId) {
        log.info("Запрос на подтверждение бронирования (id = {}) пользователем (id = {})", bookingId, ownerId);
        return bookingClient.approvedBooking(bookingId, ownerId, approved);
    }

}
