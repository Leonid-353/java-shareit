package ru.practicum.shareit.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = BookingMinimumEndDateValidator.class)
public @interface BookingMinimumEndDate {
    String message() default "Конец бронирования должен быть позже начала бронирования";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
