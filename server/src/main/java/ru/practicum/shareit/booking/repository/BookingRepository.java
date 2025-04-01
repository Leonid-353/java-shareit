package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT CASE WHEN COUNT(b.id) > 0 THEN TRUE ELSE FALSE END " +
            "FROM Booking b " +
            "WHERE b.item.id = :itemId " +
            "AND NOT (:newStart > b.end or :newEnd < b.start)")
    boolean checkBookingDate(@Param("itemId") Long itemId,
                             @Param("newStart") LocalDateTime newStart,
                             @Param("newEnd") LocalDateTime newEnd);

    boolean existsByBookerIdAndItemIdAndStatusIsAndEndIsBefore(Long authorId,
                                                               Long itemId,
                                                               BookingStatus status,
                                                               LocalDateTime localDateTime);
}
