package ru.practicum.shareit.item;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
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
import ru.practicum.shareit.item.model.commet.Comment;
import ru.practicum.shareit.item.repository.comment.CommentRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.utils.BaseTestServiceContext;
import ru.practicum.shareit.utils.TestDataUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceTest extends BaseTestServiceContext {
    private final EntityManager em;
    private final ItemService service;
    private final CommentRepository commentRepository;

    // Create
    @Test
    void createItemSuccessful() {
        NewItemRequest newItemRequest = TestDataUtils.createNewItemRequest();
        User owner = TestDataUtils.createOwner();

        ItemDto dto = service.createItem(newItemRequest, owner.getId());

        TypedQuery<Item> query = em
                .createQuery("SELECT i FROM Item i " +
                        "LEFT JOIN i.owner o " +
                        "WHERE i.id = :itemId AND o.id = :ownerId", Item.class)
                .setParameter("itemId", dto.getId())
                .setParameter("ownerId", owner.getId());
        Item result = query.getSingleResult();
        ItemDto expected = ItemMapper.mapToItemDto(result);
        assertEquals(expected, dto);
    }

    @Test
    void createItemWithItemRequestIdSuccessful() {
        NewItemRequest newItemRequest = TestDataUtils.createNewItemRequestWithItemRequestId();
        User owner = TestDataUtils.createOwner();

        ItemDto dto = service.createItem(newItemRequest, owner.getId());

        TypedQuery<Item> query = em
                .createQuery("SELECT i FROM Item i " +
                        "LEFT JOIN i.owner o " +
                        "WHERE i.id = :itemId " +
                        "AND i.requestId = :requestId " +
                        "AND o.id = :ownerId", Item.class)
                .setParameter("itemId", dto.getId())
                .setParameter("requestId", newItemRequest.getRequestId())
                .setParameter("ownerId", owner.getId());
        Item result = query.getSingleResult();
        ItemDto expected = ItemMapper.mapToItemDto(result);
        assertEquals(expected, dto);
    }

    @Test
    void createItemNoUser() {
        NewItemRequest newItemRequest = TestDataUtils.createNewItemRequest();

        assertThrows(NotFoundException.class, () -> service.createItem(newItemRequest, Long.MAX_VALUE));
    }

    @Test
    void createItemNoItemRequestId() {
        NewItemRequest newItemRequest = TestDataUtils.createNewItemRequestWithItemRequestId();
        newItemRequest.setRequestId(Long.MAX_VALUE);
        User owner = TestDataUtils.createOwner();

        assertThrows(NotFoundException.class, () -> service.createItem(newItemRequest, owner.getId()));
    }

    // Find all owner items
    @Test
    void findAllOwnerItemsSuccessful() {
        Item item = TestDataUtils.createAvailableItem();
        User owner = item.getOwner();

        List<ItemCommentsDto> items = service.findAllOwnerItems(owner.getId());

        TypedQuery<Item> query = em
                .createQuery("SELECT i FROM Item i " +
                        "LEFT JOIN i.owner o " +
                        "WHERE o.id = :ownerId", Item.class)
                .setParameter("ownerId", owner.getId());
        List<Item> resultList = query.getResultList();
        List<ItemCommentsDto> expected = ItemMapper.mapToItemCommentsDtoList(resultList);
        assertEquals(expected, items);
        assertEquals(2, items.size());
    }

    @Test
    void findAllOwnerItemsNoUser() {
        assertThrows(NotFoundException.class, () -> service.findAllOwnerItems(Long.MAX_VALUE));
    }

    // Find item
    @Test
    void findItemSuccessful() {
        Item item = TestDataUtils.createAvailableItem();
        User user = TestDataUtils.createUser();

        ItemCommentsDto dto = service.findItem(item.getId(), user.getId());

        TypedQuery<Item> query = em
                .createQuery("SELECT i FROM Item i " +
                        "WHERE i.id = :itemId", Item.class)
                .setParameter("itemId", item.getId());
        Item result = query.getSingleResult();
        ItemCommentsDto expected = ItemMapper.mapToItemCommentsDto(result);
        assertEquals(expected, dto);
    }

    @Test
    void findItemNotFound() {
        User user = TestDataUtils.createUser();

        assertThrows(NotFoundException.class, () -> service.findItem(Long.MAX_VALUE, user.getId()));
    }

    // Update item
    @Test
    void updateItemSuccessful() {
        UpdateItemRequest updateItemRequest = TestDataUtils.createUpdateItemRequest();
        Item item = TestDataUtils.createNotAvailableItem();
        User owner = item.getOwner();

        ItemDto dto = service.updateItem(updateItemRequest, item.getId(), owner.getId());

        TypedQuery<Item> query = em
                .createQuery("SELECT i FROM Item i " +
                        "LEFT JOIN i.owner o " +
                        "WHERE i.id = :itemId AND o.id = :ownerId", Item.class)
                .setParameter("itemId", item.getId())
                .setParameter("ownerId", owner.getId());
        Item result = query.getSingleResult();
        ItemDto expected = ItemMapper.mapToItemDto(result);
        assertEquals(expected, dto);
        assertTrue(dto.isAvailable());
    }

    @Test
    void updateItemNotFound() {
        UpdateItemRequest updateItemRequest = TestDataUtils.createUpdateItemRequest();
        User owner = TestDataUtils.createOwner();

        assertThrows(NotFoundException.class,
                () -> service.updateItem(updateItemRequest, Long.MAX_VALUE, owner.getId()));
    }

    @Test
    void updateItemUserNotFound() {
        UpdateItemRequest updateItemRequest = TestDataUtils.createUpdateItemRequest();
        Item item = TestDataUtils.createNotAvailableItem();

        assertThrows(NotFoundException.class,
                () -> service.updateItem(updateItemRequest, item.getId(), Long.MAX_VALUE));
    }

    @Test
    void updateItemUserNotOwner() {
        UpdateItemRequest updateItemRequest = TestDataUtils.createUpdateItemRequest();
        Item item = TestDataUtils.createNotAvailableItem();
        User user = TestDataUtils.createUser();

        assertThrows(ForbiddenOperationException.class,
                () -> service.updateItem(updateItemRequest, item.getId(), user.getId()));
    }

    // Remove item
    @Test
    void removeItemSuccessful() {
        Item item = TestDataUtils.createAvailableItem();
        User owner = item.getOwner();

        Optional<ItemCommentsDto> existingItemBeforeDeletion = Optional.of(
                service.findItem(item.getId(), owner.getId()));
        assertTrue(existingItemBeforeDeletion.isPresent());

        service.removeItem(item.getId(), owner.getId());

        assertThrows(NotFoundException.class, () -> service.findItem(item.getId(), owner.getId()));

        TypedQuery<Item> query = em
                .createQuery("SELECT i FROM Item i " +
                        "WHERE i.id = :itemId", Item.class)
                .setParameter("itemId", item.getId());
        List<Item> result = query.getResultList();
        assertTrue(result.isEmpty());
    }

    @Test
    void removeItemNotFound() {
        User owner = TestDataUtils.createOwner();

        assertThrows(NotFoundException.class,
                () -> service.removeItem(Long.MAX_VALUE, owner.getId()));
    }

    @Test
    void removeItemUserNotOwner() {
        Item item = TestDataUtils.createAvailableItem();
        User user = TestDataUtils.createUser();

        assertThrows(ForbiddenOperationException.class,
                () -> service.removeItem(item.getId(), user.getId()));
    }

    // Search items
    @Test
    void searchItemByNameOrDescriptionSuccessful() {
        Item item = TestDataUtils.createAvailableItem();
        User user = TestDataUtils.createUser();

        List<ItemDto> items = service.searchItemByNameOrDescription(TestDataUtils.TEXT_SEARCH_ITEM, user.getId());

        assertEquals(ItemMapper.mapToItemDto(item), items.getFirst());

        TypedQuery<Item> query = em
                .createQuery("SELECT i FROM Item i " +
                        "WHERE i.available = true " +
                        "AND (:text IS NOT NULL AND :text <> '' AND " +
                        "     (LOWER(i.name) LIKE LOWER(CONCAT ('%', :text, '%')) OR " +
                        "     LOWER(i.description) LIKE LOWER(CONCAT ('%', :text, '%'))))", Item.class)
                .setParameter("text", TestDataUtils.TEXT_SEARCH_ITEM);
        List<Item> resultList = query.getResultList();
        List<ItemDto> expected = ItemMapper.mapToItemDtoList(resultList);
        assertEquals(expected, items);
        assertEquals(1, items.size());
    }

    // Create comment
    @Test
    void createCommentSuccessful() {
        NewCommentRequest newCommentRequest = TestDataUtils.createNewCommentRequest();
        Item item = TestDataUtils.createAvailableItem();
        User author = TestDataUtils.createBookerAuthor();

        CommentDto dto = service.createComment(newCommentRequest, item.getId(), author.getId());

        TypedQuery<Comment> query = em
                .createQuery("SELECT c FROM Comment c " +
                        "WHERE c.id = :commentId", Comment.class)
                .setParameter("commentId", dto.getId());
        Comment result = query.getSingleResult();
        CommentDto expected = CommentMapper.mapToCommentDto(result);
        assertEquals(expected, dto);
    }

    @Test
    void createCommentNoItem() {
        NewCommentRequest newCommentRequest = TestDataUtils.createNewCommentRequest();
        User author = TestDataUtils.createBookerAuthor();

        assertThrows(NotFoundException.class,
                () -> service.createComment(newCommentRequest, Long.MAX_VALUE, author.getId()));
    }

    @Test
    void createCommentNoUser() {
        NewCommentRequest newCommentRequest = TestDataUtils.createNewCommentRequest();
        Item item = TestDataUtils.createAvailableItem();

        assertThrows(NotFoundException.class,
                () -> service.createComment(newCommentRequest, item.getId(), Long.MAX_VALUE));
    }

    @Test
    void createCommentNoBooking() {
        NewCommentRequest newCommentRequest = TestDataUtils.createNewCommentRequest();
        Item item = TestDataUtils.createNotAvailableItem();
        User author = TestDataUtils.createBookerAuthor();

        assertThrows(BadRequestException.class,
                () -> service.createComment(newCommentRequest, item.getId(), author.getId()));
    }
}