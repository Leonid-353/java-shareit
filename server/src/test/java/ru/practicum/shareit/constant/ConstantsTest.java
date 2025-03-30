package ru.practicum.shareit.constant;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConstantsTest {

    @Test
    void returnedActualHeaderName() {
        String headerName = "X-Sharer-User-Id";

        String expected = Constants.X_SHARER_USER_ID;

        assertEquals(expected, headerName);
    }
}