package ru.practicum.shareit.booking;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.QBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ForbiddenOperationException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@Transactional(readOnly = true)
public class BookingService {
    final BookingRepository bookingRepository;
    final UserRepository userRepository;
    final ItemRepository itemRepository;
    final EntityManager entityManager;

    @Autowired
    public BookingService(BookingRepository bookingRepository,
                          UserRepository userRepository,
                          ItemRepository itemRepository,
                          EntityManager entityManager) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.entityManager = entityManager;
    }

    @Transactional
    public BookingDto createBooking(NewBookingRequest newBookingRequest, Long userId) {
        Item item = itemRepository.findById(newBookingRequest.getItemId())
                .orElseThrow(
                        () -> new NotFoundException(
                                String.format("Вещь с id = %d не найдена",
                                        newBookingRequest.getItemId())
                        )
                );

        User booker = userRepository.findById(userId)
                .orElseThrow(
                        () -> new NotFoundException(
                                String.format("Пользователь с id = %d не найден", userId)
                        )
                );

        if (!item.isAvailable()) {
            throw new BadRequestException(String.format("Вещь с id = %d недоступна для бронирования", item.getId()));
        }

        try {
            Booking booking = BookingMapper.mapToBooking(newBookingRequest, booker, item);
            if (bookingRepository.existsById(item.getId())) {
                if (!bookingRepository.checkBookingDate(
                        booking.getItem().getId(),
                        booking.getStart(),
                        booking.getEnd())) {
                    throw new BadRequestException("Время бронирования пересекается с существующей бронью");
                }
            }
            booking.setItem(item);
            booking.setBooker(booker);
            bookingRepository.save(booking);
            return BookingMapper.mapToBookingDto(booking);
        } catch (RuntimeException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    public BookingDto findBooking(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new NotFoundException(String.format("Бронирование с id = %d не найдено", bookingId))
        );
        if (!Objects.equals(booking.getItem().getOwner().getId(), userId) &&
                !Objects.equals(booking.getBooker().getId(), userId)) {
            throw new ForbiddenOperationException("Просмотр доступен только владельцу вещи или автору бронирования");
        }
        return BookingMapper.mapToBookingDto(booking);
    }

    public Collection<BookingDto> findBookingsByUserId(BookingState state, Long userId, Boolean isOwner) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        QBooking booking = QBooking.booking;

        BooleanExpression notRejectedOrCanceled = booking.status.notIn(BookingStatus.REJECTED, BookingStatus.CANCELED);
        BooleanExpression waiting = booking.status.eq(BookingStatus.WAITING);
        BooleanExpression rejected = booking.status.eq(BookingStatus.REJECTED);
        BooleanExpression byItemOwnerId = booking.item.owner.id.eq(userId);
        BooleanExpression byBookerId = booking.booker.id.eq(userId);
        BooleanExpression startIsBefore = booking.start.before(LocalDateTime.now());
        BooleanExpression startIsAfter = booking.start.after(LocalDateTime.now());
        BooleanExpression endIsBefore = booking.end.before(LocalDateTime.now());
        BooleanExpression endIsAfter = booking.end.after(LocalDateTime.now());

        List<Booking> result;
        if (state == null) {
            state = BookingState.ALL;
        }
        switch (state) {
            case CURRENT -> {
                if (isOwner) {
                    result = queryFactory.selectFrom(booking)
                            .where(byItemOwnerId,
                                    startIsBefore,
                                    endIsAfter,
                                    notRejectedOrCanceled)
                            .orderBy(booking.start.desc())
                            .fetch();
                } else {
                    result = queryFactory.selectFrom(booking)
                            .where(byBookerId,
                                    startIsBefore,
                                    endIsAfter,
                                    notRejectedOrCanceled)
                            .orderBy(booking.start.desc())
                            .fetch();
                }
            }
            case PAST -> {
                if (isOwner) {
                    result = queryFactory.selectFrom(booking)
                            .where(byItemOwnerId,
                                    endIsBefore,
                                    notRejectedOrCanceled)
                            .orderBy(booking.start.desc())
                            .fetch();
                } else {
                    result = queryFactory.selectFrom(booking)
                            .where(byBookerId,
                                    endIsBefore,
                                    notRejectedOrCanceled)
                            .orderBy(booking.start.desc())
                            .fetch();
                }
            }
            case FUTURE -> {
                if (isOwner) {
                    result = queryFactory.selectFrom(booking)
                            .where(byItemOwnerId,
                                    startIsAfter,
                                    notRejectedOrCanceled)
                            .orderBy(booking.start.desc())
                            .fetch();
                } else {
                    result = queryFactory.selectFrom(booking)
                            .where(byBookerId,
                                    startIsAfter,
                                    notRejectedOrCanceled)
                            .orderBy(booking.start.desc())
                            .fetch();
                }
            }
            case WAITING -> {
                if (isOwner) {
                    result = queryFactory.selectFrom(booking)
                            .where(byItemOwnerId,
                                    waiting)
                            .orderBy(booking.start.desc())
                            .fetch();
                } else {
                    result = queryFactory.selectFrom(booking)
                            .where(byBookerId,
                                    waiting)
                            .orderBy(booking.start.desc())
                            .fetch();
                }
            }
            case REJECTED -> {
                if (isOwner) {
                    result = queryFactory.selectFrom(booking)
                            .where(byItemOwnerId,
                                    rejected)
                            .orderBy(booking.start.desc())
                            .fetch();
                } else {
                    result = queryFactory.selectFrom(booking)
                            .where(byBookerId,
                                    rejected)
                            .orderBy(booking.start.desc())
                            .fetch();
                }
            }
            default -> {
                if (isOwner) {
                    result = queryFactory.selectFrom(booking)
                            .where(byItemOwnerId)
                            .orderBy(booking.start.desc())
                            .fetch();
                } else {
                    result = queryFactory.selectFrom(booking)
                            .where(byBookerId)
                            .orderBy(booking.start.desc())
                            .fetch();
                }
            }
        }

        if (result.isEmpty()) {
            throw new NotFoundException("Бронирования не найдены");
        }

        return result.stream().map(BookingMapper::mapToBookingDto).toList();
    }

    @Transactional
    public BookingDto approvedBooking(Long bookingId,
                                      Long ownerId,
                                      Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new NotFoundException(String.format("Бронирование с id = %d не найдено", bookingId))
        );
        if (!Objects.equals(booking.getItem().getOwner().getId(), ownerId)) {
            throw new BadRequestException("Подвердить бронирование может только владелец вещи");
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);

        bookingRepository.save(booking);

        return BookingMapper.mapToBookingDto(booking);
    }
}
