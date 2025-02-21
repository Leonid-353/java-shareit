package ru.practicum.shareit.exception;

public class ForbiddenOperationException extends RuntimeException {
    public ForbiddenOperationException(final String message) {
        super(message);
    }

    public ForbiddenOperationException(final String message, Throwable e) {
        super(message, e);
    }
}
