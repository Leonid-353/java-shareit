package ru.practicum.shareit.booking.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.utils.TestDataUtils;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingRepositoryTest {
    final BookingRepository repository;

    @Test
    void checkBookingDate() {
        NewBookingRequest newBookingRequest = TestDataUtils.createNewBookingRequestAvailableItem();

        assertFalse(repository.checkBookingDate(
                newBookingRequest.getItemId(),
                newBookingRequest.getStart(),
                newBookingRequest.getEnd()
        ));
    }

    @Test
    void existsByBookerIdAndItemIdAndStatusIsAndEndIsBefore() {
        User booker = TestDataUtils.createBookerAuthor();
        Item item = TestDataUtils.createAvailableItem();

        assertTrue(repository.existsByBookerIdAndItemIdAndStatusIsAndEndIsBefore(
                booker.getId(),
                item.getId(),
                BookingStatus.APPROVED,
                LocalDateTime.now()
        ));
    }
}