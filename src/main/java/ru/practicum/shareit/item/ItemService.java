package ru.practicum.shareit.item;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.QBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ForbiddenOperationException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemCommentsDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.dto.comment.NewCommentRequest;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.mapper.comment.CommentMapper;
import ru.practicum.shareit.item.model.BookingDates;
import ru.practicum.shareit.item.model.commet.Comment;
import ru.practicum.shareit.item.model.commet.QComment;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.repository.comment.CommentRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@Transactional(readOnly = true)
public class ItemService {
    final ItemRepository itemRepository;
    final UserRepository userRepository;
    final BookingRepository bookingRepository;
    final CommentRepository commentRepository;
    final EntityManager entityManager;

    @Autowired
    public ItemService(ItemRepository itemRepository,
                       UserRepository userRepository,
                       BookingRepository bookingRepository,
                       CommentRepository commentRepository,
                       EntityManager entityManager) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
        this.entityManager = entityManager;
    }

    @Transactional
    public ItemDto createItem(NewItemRequest newItemRequest, Long ownerId) {
        Item item = ItemMapper.mapToItem(newItemRequest, userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %d не найден", ownerId))));
        itemRepository.save(item);
        return ItemMapper.mapToItemDto(item);
    }

    public Collection<ItemCommentsDto> findAllOwnerItems(Long ownerId) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        QItem qItem = QItem.item;
        QBooking qBooking = QBooking.booking;
        QComment qComment = QComment.comment;

        List<Item> items;
        if (userRepository.existsById(ownerId)) {
            items = queryFactory.selectFrom(qItem)
                    .where(qItem.owner.id.eq(ownerId))
                    .fetch();
        } else {
            throw new NotFoundException(String.format("Пользователь с id = %d не найден", ownerId));
        }

        List<Booking> finishedBookings = queryFactory.selectFrom(qBooking)
                .where(qBooking.item.id.in(items
                                .stream()
                                .map(Item::getId)
                                .toList()),
                        qBooking.end.before(LocalDateTime.now()))
                .orderBy(qBooking.end.desc())
                .fetch();
        List<Booking> nextBookings = queryFactory.selectFrom(qBooking)
                .where(qBooking.item.id.in(items
                                .stream()
                                .map(Item::getId)
                                .toList()),
                        qBooking.start.after(LocalDateTime.now()))
                .orderBy(qBooking.start.asc())
                .fetch();

        List<Comment> comments = queryFactory.selectFrom(qComment)
                .where(qComment.item.id.in(items
                        .stream()
                        .map(Item::getId)
                        .toList()))
                .fetch();
        return items.stream()
                .map(item -> {
                    Booking lastBooking = finishedBookings.stream()
                            .filter(booking -> Objects.equals(booking.getItem().getId(), item.getId()))
                            .findFirst()
                            .orElse(null);
                    Booking nextBooking = nextBookings.stream()
                            .filter(booking -> Objects.equals(booking.getItem().getId(), item.getId()))
                            .findFirst()
                            .orElse(null);
                    if (lastBooking != null) {
                        item.setLastBooking(new BookingDates(lastBooking.getStart(), lastBooking.getEnd()));
                    }
                    if (nextBooking != null) {
                        item.setNextBooking(new BookingDates(nextBooking.getStart(), nextBooking.getEnd()));
                    }
                    comments.forEach(comment -> {
                        if (Objects.equals(comment.getItem().getId(), item.getId())) {
                            item.addComment(comment);
                        }
                    });
                    return ItemMapper.mapToItemCommentsDto(item);
                })
                .toList();
    }

    public ItemCommentsDto findItem(Long itemId, Long userId) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        QComment qComment = QComment.comment;

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Вещь с id = %d не найдена", itemId)));
        List<Comment> comments = queryFactory.selectFrom(qComment)
                .where(qComment.item.id.eq(itemId))
                .fetch();
        comments.forEach(comment -> {
            if (Objects.equals(comment.getItem().getId(), item.getId())) {
                item.addComment(comment);
            }
        });
        return ItemMapper.mapToItemCommentsDto(item);
    }

    @Transactional
    public ItemDto updateItem(UpdateItemRequest updateItemRequest,
                              Long itemId,
                              Long ownerId) {
        Item updatedItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Вещь с id = %d не найдена", itemId)));
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %d не найден", ownerId)));

        if (!Objects.equals(updatedItem.getOwner().getId(), ownerId)) {
            throw new ForbiddenOperationException("Изменять вещь может только её владелец");
        }

        ItemMapper.updateItemFields(updatedItem, updateItemRequest);
        updatedItem.setOwner(owner);

        itemRepository.save(updatedItem);
        return ItemMapper.mapToItemDto(updatedItem);
    }

    @Transactional
    public void removeItem(Long itemId,
                           Long ownerId) {
        Item removedItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Вещь с id = %d не найдена", itemId)));

        if (!Objects.equals(removedItem.getOwner().getId(), ownerId)) {
            throw new ForbiddenOperationException("Удалять вещь может только её владелец");
        }

        itemRepository.deleteById(itemId);
    }

    public Collection<ItemDto> searchItemByNameOrDescription(String text,
                                                             Long userId) {
        return itemRepository.search(text).stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    @Transactional
    public CommentDto createComment(NewCommentRequest newCommentRequest,
                                    Long itemId,
                                    Long authorId) {
        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new NotFoundException(String.format("Вещь с id = %d не найдена", itemId))
        );
        User author = userRepository.findById(authorId).orElseThrow(
                () -> new NotFoundException(String.format("Пользователь с id = %d не найден", authorId))
        );

        boolean check = bookingRepository.existsByBookerIdAndItemIdAndStatusIsAndEndIsBefore(
                authorId,
                itemId,
                BookingStatus.APPROVED,
                newCommentRequest.getCreated());

        if (!check) {
            throw new BadRequestException(
                    String.format("Пользователь (id = %d) не арендовал вещь (id = %d)", authorId, itemId)
            );
        }

        Comment comment = CommentMapper.mapToComment(newCommentRequest, author);
        item.addComment(comment);

        commentRepository.save(comment);

        return CommentMapper.mapToCommentDto(comment);
    }
}
