package ru.practicum.shareit.booking;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ForbiddenOperationException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.utils.BaseTestServiceContext;
import ru.practicum.shareit.utils.TestDataUtils;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceTest extends BaseTestServiceContext {
    private final EntityManager em;
    private final BookingService service;

    // Create
    @Test
    void createBookingSuccessful() {
        NewBookingRequest newBookingRequest = TestDataUtils.createNewBookingRequestAvailableItem();
        User user = TestDataUtils.createUser();

        BookingDto dto = service.createBooking(newBookingRequest, user.getId());

        TypedQuery<Booking> query = em
                .createQuery("SELECT b FROM Booking b " +
                        "LEFT JOIN b.item i " +
                        "WHERE b.id = :bookingId AND i.id = :itemId", Booking.class);
        query.setParameter("bookingId", dto.getId());
        query.setParameter("itemId", newBookingRequest.getItemId());
        Booking result = query.getSingleResult();
        BookingDto expected = BookingMapper.mapToBookingDto(result);
        assertEquals(expected, dto);
    }

    @Test
    void createBookingNoItem() {
        NewBookingRequest newBookingRequest = TestDataUtils.createNewBookingRequestNoItem();
        User user = TestDataUtils.createUser();

        assertThrows(NotFoundException.class, () -> service.createBooking(newBookingRequest, user.getId()));
    }

    @Test
    void createBookingNoUser() {
        NewBookingRequest newBookingRequest = TestDataUtils.createNewBookingRequestAvailableItem();

        assertThrows(NotFoundException.class, () -> service.createBooking(newBookingRequest, 5L));
    }

    @Test
    void createBookingNotAvailableItem() {
        NewBookingRequest newBookingRequest = TestDataUtils.createNewBookingRequestNotAvailableItem();
        User user = TestDataUtils.createUser();

        assertThrows(BadRequestException.class, () -> service.createBooking(newBookingRequest, user.getId()));
    }

    @Test
    void createBookingDateIntersection() {
        NewBookingRequest newBookingRequest = TestDataUtils.createNewBookingRequestDateIntersection();
        User user = TestDataUtils.createUser();

        assertThrows(BadRequestException.class, () -> service.createBooking(newBookingRequest, user.getId()));
    }

    // Find booking
    @Test
    void findBooking() {
        Booking booking = TestDataUtils.createBookingAvailableItem();
        User owner = booking.getItem().getOwner();

        BookingDto dto = service.findBooking(booking.getId(), owner.getId());

        assertEquals(BookingMapper.mapToBookingDto(booking), dto);

        TypedQuery<Booking> query = em
                .createQuery("SELECT b FROM Booking b " +
                        "LEFT JOIN b.item i " +
                        "LEFT JOIN i.owner o " +
                        "WHERE b.id = :bookingId AND o.id = :ownerId", Booking.class);
        query.setParameter("bookingId", dto.getId());
        query.setParameter("ownerId", owner.getId());
        Booking result = query.getSingleResult();
        BookingDto expected = BookingMapper.mapToBookingDto(result);
        assertEquals(expected, dto);
    }

    @Test
    void findBookingNotFound() {
        User booker = TestDataUtils.createBookerAuthor();

        assertThrows(NotFoundException.class, () -> service.findBooking(Long.MAX_VALUE, booker.getId()));
    }

    @Test
    void findBookingForbiddenOperationNotOwnerOrBooker() {
        Booking booking = TestDataUtils.createBookingAvailableItem();
        User user = TestDataUtils.createUser();

        assertThrows(ForbiddenOperationException.class, () -> service.findBooking(booking.getId(), user.getId()));
    }

    // Find bookings by Owner
    @Test
    void findBookingsByOwnerIdStateAll() {
        Booking booking = TestDataUtils.createBookingAvailableItem();
        User owner = booking.getItem().getOwner();
        BookingState bookingState = BookingState.ALL;

        List<BookingDto> bookings = service.findBookingsByUserId(bookingState, owner.getId(), true);

        assertEquals(BookingMapper.mapToBookingDto(booking), bookings.getFirst());

        TypedQuery<Booking> query = em
                .createQuery("SELECT b FROM Booking b " +
                        "LEFT JOIN b.item i " +
                        "LEFT JOIN i.owner o " +
                        "WHERE o.id = :ownerId", Booking.class);
        query.setParameter("ownerId", owner.getId());
        List<Booking> resultList = query.getResultList();
        List<BookingDto> expected = BookingMapper.mapToBookingDtoList(resultList);
        assertEquals(expected, bookings);
        assertEquals(4, bookings.size());
    }

    @Test
    void findBookingsByOwnerIdStateCurrent() {
        Booking booking = TestDataUtils.createBookingAvailableItemStateCurrent();
        User owner = booking.getItem().getOwner();
        BookingState bookingState = BookingState.CURRENT;

        List<BookingDto> bookings = service.findBookingsByUserId(bookingState, owner.getId(), true);

        assertEquals(BookingMapper.mapToBookingDto(booking), bookings.getFirst());

        TypedQuery<Booking> query = em
                .createQuery("SELECT b FROM Booking b " +
                        "LEFT JOIN b.item i " +
                        "LEFT JOIN i.owner o " +
                        "WHERE o.id = :ownerId " +
                        "AND :now BETWEEN b.start AND b.end " +
                        "AND b.status NOT IN (:rejected, :canceled)", Booking.class);
        query.setParameter("ownerId", owner.getId());
        query.setParameter("now", LocalDateTime.now());
        query.setParameter("rejected", BookingStatus.REJECTED);
        query.setParameter("canceled", BookingStatus.CANCELED);
        List<Booking> resultList = query.getResultList();
        List<BookingDto> expected = BookingMapper.mapToBookingDtoList(resultList);
        assertEquals(expected, bookings);
        assertEquals(1, bookings.size());
    }

    @Test
    void findBookingsByOwnerIdStatePast() {
        Booking booking = TestDataUtils.createBookingAvailableItemStatePast();
        User owner = booking.getItem().getOwner();
        BookingState bookingState = BookingState.PAST;

        List<BookingDto> bookings = service.findBookingsByUserId(bookingState, owner.getId(), true);

        assertEquals(BookingMapper.mapToBookingDto(booking), bookings.getFirst());

        TypedQuery<Booking> query = em
                .createQuery("SELECT b FROM Booking b " +
                        "LEFT JOIN b.item i " +
                        "LEFT JOIN i.owner o " +
                        "WHERE o.id = :ownerId " +
                        "AND b.end < :now " +
                        "AND b.status NOT IN (:rejected, :canceled)", Booking.class);
        query.setParameter("ownerId", owner.getId());
        query.setParameter("now", LocalDateTime.now());
        query.setParameter("rejected", BookingStatus.REJECTED);
        query.setParameter("canceled", BookingStatus.CANCELED);
        List<Booking> resultList = query.getResultList();
        List<BookingDto> expected = BookingMapper.mapToBookingDtoList(resultList);
        assertEquals(expected, bookings);
        assertEquals(1, bookings.size());
    }

    @Test
    void findBookingsByOwnerIdStateFuture() {
        Booking booking = TestDataUtils.createBookingAvailableItem();
        User owner = booking.getItem().getOwner();
        BookingState bookingState = BookingState.FUTURE;

        List<BookingDto> bookings = service.findBookingsByUserId(bookingState, owner.getId(), true);

        assertEquals(BookingMapper.mapToBookingDto(booking), bookings.getFirst());

        TypedQuery<Booking> query = em
                .createQuery("SELECT b FROM Booking b " +
                        "LEFT JOIN b.item i " +
                        "LEFT JOIN i.owner o " +
                        "WHERE o.id = :ownerId " +
                        "AND b.start > :now " +
                        "AND b.status NOT IN (:rejected, :canceled)", Booking.class);
        query.setParameter("ownerId", owner.getId());
        query.setParameter("now", LocalDateTime.now());
        query.setParameter("rejected", BookingStatus.REJECTED);
        query.setParameter("canceled", BookingStatus.CANCELED);
        List<Booking> resultList = query.getResultList();
        List<BookingDto> expected = BookingMapper.mapToBookingDtoList(resultList);
        assertEquals(expected, bookings);
        assertEquals(1, bookings.size());
    }

    @Test
    void findBookingsByOwnerIdStateWaiting() {
        Booking booking = TestDataUtils.createBookingAvailableItem();
        User owner = booking.getItem().getOwner();
        BookingState bookingState = BookingState.WAITING;

        List<BookingDto> bookings = service.findBookingsByUserId(bookingState, owner.getId(), true);

        assertEquals(BookingMapper.mapToBookingDto(booking), bookings.getFirst());

        TypedQuery<Booking> query = em
                .createQuery("SELECT b FROM Booking b " +
                        "LEFT JOIN b.item i " +
                        "LEFT JOIN i.owner o " +
                        "WHERE o.id = :ownerId " +
                        "AND b.status = :waiting", Booking.class);
        query.setParameter("ownerId", owner.getId());
        query.setParameter("waiting", BookingStatus.WAITING);
        List<Booking> resultList = query.getResultList();
        List<BookingDto> expected = BookingMapper.mapToBookingDtoList(resultList);
        assertEquals(expected, bookings);
        assertEquals(1, bookings.size());
    }

    @Test
    void findBookingsByOwnerIdStateRejected() {
        Booking booking = TestDataUtils.createBookingAvailableItemStateRejected();
        User owner = booking.getItem().getOwner();
        BookingState bookingState = BookingState.REJECTED;

        List<BookingDto> bookings = service.findBookingsByUserId(bookingState, owner.getId(), true);

        assertEquals(BookingMapper.mapToBookingDto(booking), bookings.getFirst());

        TypedQuery<Booking> query = em
                .createQuery("SELECT b FROM Booking b " +
                        "LEFT JOIN b.item i " +
                        "LEFT JOIN i.owner o " +
                        "WHERE o.id = :ownerId " +
                        "AND b.status = :rejected", Booking.class);
        query.setParameter("ownerId", owner.getId());
        query.setParameter("rejected", BookingStatus.REJECTED);
        List<Booking> resultList = query.getResultList();
        List<BookingDto> expected = BookingMapper.mapToBookingDtoList(resultList);
        assertEquals(expected, bookings);
        assertEquals(1, bookings.size());
    }

    @Test
    void findBookingsByOwnerIdStateNull() {
        Booking booking = TestDataUtils.createBookingAvailableItem();
        User owner = booking.getItem().getOwner();

        List<BookingDto> bookings = service.findBookingsByUserId(null, owner.getId(), true);

        assertEquals(BookingMapper.mapToBookingDto(booking), bookings.getFirst());

        TypedQuery<Booking> query = em
                .createQuery("SELECT b FROM Booking b " +
                        "LEFT JOIN b.item i " +
                        "LEFT JOIN i.owner o " +
                        "WHERE o.id = :ownerId", Booking.class);
        query.setParameter("ownerId", owner.getId());
        List<Booking> resultList = query.getResultList();
        List<BookingDto> expected = BookingMapper.mapToBookingDtoList(resultList);
        assertEquals(expected, bookings);
        assertEquals(4, bookings.size());
    }

    // Find Bookings by Booker
    @Test
    void findBookingsByBookerIdStateAll() {
        Booking booking = TestDataUtils.createBookingAvailableItem();
        User booker = booking.getBooker();
        BookingState bookingState = BookingState.ALL;

        List<BookingDto> bookings = service.findBookingsByUserId(bookingState, booker.getId(), false);

        assertEquals(BookingMapper.mapToBookingDto(booking), bookings.getFirst());

        TypedQuery<Booking> query = em
                .createQuery("SELECT b FROM Booking b " +
                        "LEFT JOIN b.booker u " +
                        "WHERE u.id = :bookerId", Booking.class);
        query.setParameter("bookerId", booker.getId());
        List<Booking> resultList = query.getResultList();
        List<BookingDto> expected = BookingMapper.mapToBookingDtoList(resultList);
        assertEquals(expected, bookings);
        assertEquals(4, bookings.size());
    }

    @Test
    void findBookingsByBookerIdStateCurrent() {
        Booking booking = TestDataUtils.createBookingAvailableItemStateCurrent();
        User booker = booking.getBooker();
        BookingState bookingState = BookingState.CURRENT;

        List<BookingDto> bookings = service.findBookingsByUserId(bookingState, booker.getId(), false);

        assertEquals(BookingMapper.mapToBookingDto(booking), bookings.getFirst());

        TypedQuery<Booking> query = em
                .createQuery("SELECT b FROM Booking b " +
                        "LEFT JOIN b.booker u " +
                        "WHERE u.id = :bookerId " +
                        "AND :now BETWEEN b.start AND b.end " +
                        "AND b.status NOT IN (:rejected, :canceled)", Booking.class);
        query.setParameter("bookerId", booker.getId());
        query.setParameter("now", LocalDateTime.now());
        query.setParameter("rejected", BookingStatus.REJECTED);
        query.setParameter("canceled", BookingStatus.CANCELED);
        List<Booking> resultList = query.getResultList();
        List<BookingDto> expected = BookingMapper.mapToBookingDtoList(resultList);
        assertEquals(expected, bookings);
        assertEquals(1, bookings.size());
    }

    @Test
    void findBookingsByBookerIdStatePast() {
        Booking booking = TestDataUtils.createBookingAvailableItemStatePast();
        User booker = booking.getBooker();
        BookingState bookingState = BookingState.PAST;

        List<BookingDto> bookings = service.findBookingsByUserId(bookingState, booker.getId(), false);

        assertEquals(BookingMapper.mapToBookingDto(booking), bookings.getFirst());

        TypedQuery<Booking> query = em
                .createQuery("SELECT b FROM Booking b " +
                        "LEFT JOIN b.booker u " +
                        "WHERE u.id = :bookerId " +
                        "AND b.end < :now " +
                        "AND b.status NOT IN (:rejected, :canceled)", Booking.class);
        query.setParameter("bookerId", booker.getId());
        query.setParameter("now", LocalDateTime.now());
        query.setParameter("rejected", BookingStatus.REJECTED);
        query.setParameter("canceled", BookingStatus.CANCELED);
        List<Booking> resultList = query.getResultList();
        List<BookingDto> expected = BookingMapper.mapToBookingDtoList(resultList);
        assertEquals(expected, bookings);
        assertEquals(1, bookings.size());
    }

    @Test
    void findBookingsByBookerIdStateFuture() {
        Booking booking = TestDataUtils.createBookingAvailableItem();
        User booker = booking.getBooker();
        BookingState bookingState = BookingState.FUTURE;

        List<BookingDto> bookings = service.findBookingsByUserId(bookingState, booker.getId(), false);

        assertEquals(BookingMapper.mapToBookingDto(booking), bookings.getFirst());

        TypedQuery<Booking> query = em
                .createQuery("SELECT b FROM Booking b " +
                        "LEFT JOIN b.booker u " +
                        "WHERE u.id = :bookerId " +
                        "AND b.start > :now " +
                        "AND b.status NOT IN (:rejected, :canceled)", Booking.class);
        query.setParameter("bookerId", booker.getId());
        query.setParameter("now", LocalDateTime.now());
        query.setParameter("rejected", BookingStatus.REJECTED);
        query.setParameter("canceled", BookingStatus.CANCELED);
        List<Booking> resultList = query.getResultList();
        List<BookingDto> expected = BookingMapper.mapToBookingDtoList(resultList);
        assertEquals(expected, bookings);
        assertEquals(1, bookings.size());
    }

    @Test
    void findBookingsByBookerIdStateWaiting() {
        Booking booking = TestDataUtils.createBookingAvailableItem();
        User booker = booking.getBooker();
        BookingState bookingState = BookingState.WAITING;

        List<BookingDto> bookings = service.findBookingsByUserId(bookingState, booker.getId(), false);

        assertEquals(BookingMapper.mapToBookingDto(booking), bookings.getFirst());

        TypedQuery<Booking> query = em
                .createQuery("SELECT b FROM Booking b " +
                        "LEFT JOIN b.booker u " +
                        "WHERE u.id = :bookerId " +
                        "AND b.status = :waiting", Booking.class);
        query.setParameter("bookerId", booker.getId());
        query.setParameter("waiting", BookingStatus.WAITING);
        List<Booking> resultList = query.getResultList();
        List<BookingDto> expected = BookingMapper.mapToBookingDtoList(resultList);
        assertEquals(expected, bookings);
        assertEquals(1, bookings.size());
    }

    @Test
    void findBookingsByBookerIdStateRejected() {
        Booking booking = TestDataUtils.createBookingAvailableItemStateRejected();
        User booker = booking.getBooker();
        BookingState bookingState = BookingState.REJECTED;

        List<BookingDto> bookings = service.findBookingsByUserId(bookingState, booker.getId(), false);

        assertEquals(BookingMapper.mapToBookingDto(booking), bookings.getFirst());

        TypedQuery<Booking> query = em
                .createQuery("SELECT b FROM Booking b " +
                        "LEFT JOIN b.booker u " +
                        "WHERE u.id = :bookerId " +
                        "AND b.status = :rejected", Booking.class);
        query.setParameter("bookerId", booker.getId());
        query.setParameter("rejected", BookingStatus.REJECTED);
        List<Booking> resultList = query.getResultList();
        List<BookingDto> expected = BookingMapper.mapToBookingDtoList(resultList);
        assertEquals(expected, bookings);
        assertEquals(1, bookings.size());
    }

    @Test
    void findBookingsByUserIdNotFound() {
        User user = TestDataUtils.createUser();

        assertThrows(NotFoundException.class,
                () -> service.findBookingsByUserId(null, user.getId(), false));
    }

    @Test
    void approvedBookingSuccessful() {
        Booking booking = TestDataUtils.createBookingAvailableItem();
        User owner = booking.getItem().getOwner();

        BookingDto dto = service.approvedBooking(booking.getId(), owner.getId(), true);

        TypedQuery<Booking> query = em
                .createQuery("SELECT b FROM Booking b " +
                        "LEFT JOIN b.item i " +
                        "LEFT JOIN i.owner o " +
                        "WHERE b.id = :bookingId " +
                        "AND o.id = :ownerId", Booking.class);
        query.setParameter("bookingId", booking.getId());
        query.setParameter("ownerId", owner.getId());
        Booking result = query.getSingleResult();
        BookingDto expected = BookingMapper.mapToBookingDto(result);
        assertEquals(expected, dto);
        assertEquals(expected.getStatus(), dto.getStatus());
        assertEquals(BookingStatus.APPROVED, dto.getStatus());
    }

    @Test
    void rejectedBookingSuccessful() {
        Booking booking = TestDataUtils.createBookingAvailableItem();
        User owner = booking.getItem().getOwner();

        BookingDto dto = service.approvedBooking(booking.getId(), owner.getId(), false);

        TypedQuery<Booking> query = em
                .createQuery("SELECT b FROM Booking b " +
                        "LEFT JOIN b.item i " +
                        "LEFT JOIN i.owner o " +
                        "WHERE b.id = :bookingId " +
                        "AND o.id = :ownerId", Booking.class);
        query.setParameter("bookingId", booking.getId());
        query.setParameter("ownerId", owner.getId());
        Booking result = query.getSingleResult();
        BookingDto expected = BookingMapper.mapToBookingDto(result);
        assertEquals(expected, dto);
        assertEquals(expected.getStatus(), dto.getStatus());
        assertEquals(BookingStatus.REJECTED, dto.getStatus());
    }

    @Test
    void approvedBookingNotFound() {
        User owner = TestDataUtils.createOwner();

        assertThrows(NotFoundException.class,
                () -> service.approvedBooking(Long.MAX_VALUE, owner.getId(), true));
    }

    @Test
    void approvedBookingBadUserId() {
        Booking booking = TestDataUtils.createBookingAvailableItem();
        User user = TestDataUtils.createUser();

        assertThrows(BadRequestException.class,
                () -> service.approvedBooking(booking.getId(), user.getId(), true));
    }
}