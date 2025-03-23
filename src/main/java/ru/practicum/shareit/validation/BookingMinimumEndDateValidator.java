package ru.practicum.shareit.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.practicum.shareit.booking.dto.NewBookingRequest;

import java.time.LocalDateTime;

public class BookingMinimumEndDateValidator implements ConstraintValidator<BookingMinimumEndDate, NewBookingRequest> {

    @Override
    public void initialize(BookingMinimumEndDate constraintAnnotation) {
    }

    @Override
    public boolean isValid(NewBookingRequest booking, ConstraintValidatorContext context) {
        if (booking == null) {
            return false;
        }

        LocalDateTime start = booking.getStart();
        LocalDateTime end = booking.getEnd();

        if (start == null || end == null) {
            return false;
        }
        return start.isBefore(end);
    }

}
