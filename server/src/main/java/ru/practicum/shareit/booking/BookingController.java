package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.constant.Constants;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@Slf4j
@RestController
@RequestMapping("/bookings")
public class BookingController {
    final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto createBooking(@RequestBody NewBookingRequest newBookingRequest,
                                    @RequestHeader(Constants.X_SHARER_USER_ID) Long userId) {
        log.info("Полученное тело запроса на создание бронирования: {}", newBookingRequest.toString());
        return bookingService.createBooking(newBookingRequest, userId);
    }

    @GetMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingDto findBooking(@PathVariable("bookingId") Long bookingId,
                                  @RequestHeader(Constants.X_SHARER_USER_ID) Long userId) {
        log.info("Запрос на получение бронирования (id = {}) пользователем (id = {})", bookingId, userId);
        return bookingService.findBooking(bookingId, userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<BookingDto> findBookings(@RequestParam(required = false) BookingState state,
                                         @RequestHeader(Constants.X_SHARER_USER_ID) Long userId) {
        log.info("Запрос на получение всех бронирований пользователя (id = {})", userId);
        return bookingService.findBookingsByUserId(state, userId, false);
    }

    @GetMapping("/owner")
    @ResponseStatus(HttpStatus.OK)
    public List<BookingDto> findBookingsForOwner(@RequestParam(required = false) BookingState state,
                                                 @RequestHeader(Constants.X_SHARER_USER_ID) Long ownerId) {
        log.info("Запрос на получение всех бронирований вещей владельца (id = {})", ownerId);
        return bookingService.findBookingsByUserId(state, ownerId, true);
    }

    @PatchMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingDto approvedBooking(@PathVariable("bookingId") Long bookingId,
                                      @RequestParam Boolean approved,
                                      @RequestHeader(Constants.X_SHARER_USER_ID) Long ownerId) {
        log.info("Запрос на подтверждение бронирования (id = {}) пользователем (id = {})", bookingId, ownerId);
        return bookingService.approvedBooking(bookingId, ownerId, approved);
    }

}
