package ru.practicum.shareit.exception;

public class DuplicatedDataException extends RuntimeException {
    public DuplicatedDataException(final String message) {
        super(message);
    }

    public DuplicatedDataException(final String message, Throwable e) {
        super(message, e);
    }
}
