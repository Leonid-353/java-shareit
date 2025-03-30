package ru.practicum.shareit.request;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.dto.response.ItemRequestWithResponsesDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.utils.BaseTestServiceContext;
import ru.practicum.shareit.utils.TestDataUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceTest extends BaseTestServiceContext {
    private final EntityManager em;
    private final ItemRequestService service;

    // Create itemRequest
    @Test
    void createItemRequestSuccessful() {
        NewItemRequestDto newItemRequestDto = TestDataUtils.createNewItemRequestDto();
        User requestor = TestDataUtils.createRequestor();

        ItemRequestDto dto = service.createItemRequest(newItemRequestDto, requestor.getId());

        TypedQuery<ItemRequest> query = em
                .createQuery("SELECT ir FROM ItemRequest ir " +
                        "WHERE ir.id = :itemRequestId", ItemRequest.class)
                .setParameter("itemRequestId", dto.getId());
        ItemRequest result = query.getSingleResult();
        ItemRequestDto expected = ItemRequestMapper.mapToItemRequestDto(result);
        assertEquals(expected, dto);
    }

    @Test
    void createItemRequestNoUser() {
        NewItemRequestDto newItemRequestDto = TestDataUtils.createNewItemRequestDto();

        assertThrows(NotFoundException.class,
                () -> service.createItemRequest(newItemRequestDto, Long.MAX_VALUE));
    }

    // Find all itemRequests for requestor
    @Test
    void findAllItemRequestsForRequestorSuccessful() {
        ItemRequest itemRequest = TestDataUtils.createItemRequest();
        User requestor = itemRequest.getRequestor();

        List<ItemRequestWithResponsesDto> itemRequests = service
                .findAllItemRequestsForRequestor(requestor.getId());

        TypedQuery<ItemRequest> query = em
                .createQuery("SELECT ir FROM ItemRequest ir " +
                        "LEFT JOIN ir.requestor r " +
                        "WHERE ir.id = :itemRequestId " +
                        "AND r.id = :requestorId", ItemRequest.class)
                .setParameter("itemRequestId", itemRequest.getId())
                .setParameter("requestorId", requestor.getId());
        List<ItemRequest> resultList = query.getResultList();
        List<ItemRequestWithResponsesDto> expected = ItemRequestMapper
                .mapToItemRequestWithResponsesDtoList(resultList);
        assertEquals(expected.getFirst().getId(), itemRequests.getFirst().getId());
        assertEquals(expected.getFirst().getDescription(), itemRequests.getFirst().getDescription());
        assertEquals(expected.getFirst().getRequestor(), itemRequests.getFirst().getRequestor());
        assertEquals(expected.getFirst().getCreated(), itemRequests.getFirst().getCreated());
        assertEquals(1, itemRequests.size());
    }

    @Test
    void findAllItemRequestsForRequestorNoUser() {
        assertThrows(NotFoundException.class,
                () -> service.findAllItemRequestsForRequestor(Long.MAX_VALUE));
    }

    @Test
    void findAllItemRequestsForRequestorNoResponses() {
        ItemRequest itemRequest = TestDataUtils.createItemRequestNoResponses();
        User owner = TestDataUtils.createOwner();

        List<ItemRequestWithResponsesDto> itemRequests = service
                .findAllItemRequestsForRequestor(owner.getId());

        TypedQuery<ItemRequest> query = em
                .createQuery("SELECT ir FROM ItemRequest ir " +
                        "LEFT JOIN ir.requestor r " +
                        "WHERE ir.id = :itemRequestId " +
                        "AND r.id = :requestorId", ItemRequest.class)
                .setParameter("itemRequestId", itemRequest.getId())
                .setParameter("requestorId", owner.getId());
        List<ItemRequest> resultList = query.getResultList();
        List<ItemRequestWithResponsesDto> expected = ItemRequestMapper
                .mapToItemRequestWithResponsesDtoList(resultList);
        assertEquals(1, itemRequests.size());
        assertEquals(expected.getFirst().getItems(), itemRequests.getFirst().getItems());
        assertTrue(itemRequests.getFirst().getItems().isEmpty());
    }

    // Find all itemRequests
    @Test
    void findAllItemRequestsSuccessful() {
        User user = TestDataUtils.createUser();

        List<ItemRequestDto> itemRequests = service.findAllItemRequests(user.getId());

        TypedQuery<ItemRequest> query = em
                .createQuery("SELECT ir FROM ItemRequest ir " +
                        "ORDER BY ir.created DESC", ItemRequest.class);
        List<ItemRequest> resultList = query.getResultList();
        List<ItemRequestDto> expected = ItemRequestMapper.mapToItemRequestDtoList(resultList);
        assertEquals(expected, itemRequests);
        assertEquals(2, itemRequests.size());
    }

    // Find itemRequest
    @Test
    void findItemRequestSuccessful() {
        ItemRequest itemRequest = TestDataUtils.createItemRequest();
        User owner = TestDataUtils.createOwner();

        ItemRequestWithResponsesDto dto = service.findItemRequest(itemRequest.getId(), owner.getId());

        TypedQuery<ItemRequest> query = em
                .createQuery("SELECT ir FROM ItemRequest ir " +
                        "WHERE ir.id = :itemRequestId", ItemRequest.class)
                .setParameter("itemRequestId", itemRequest.getId());
        ItemRequest result = query.getSingleResult();
        ItemRequestWithResponsesDto expected = ItemRequestMapper.mapToItemRequestWithResponsesDto(result);
        assertEquals(expected.getId(), dto.getId());
        assertEquals(expected.getDescription(), dto.getDescription());
        assertEquals(expected.getRequestor(), dto.getRequestor());
        assertEquals(expected.getCreated(), dto.getCreated());
    }

    @Test
    void findItemRequestNotFound() {
        User owner = TestDataUtils.createOwner();

        assertThrows(NotFoundException.class,
                () -> service.findItemRequest(Long.MAX_VALUE, owner.getId()));
    }

    @Test
    void findItemRequestNoUser() {
        ItemRequest itemRequest = TestDataUtils.createItemRequestNoResponses();

        assertThrows(NotFoundException.class,
                () -> service.findItemRequest(itemRequest.getId(), Long.MAX_VALUE));
    }

    @Test
    void findItemRequestNoResponses() {
        ItemRequest itemRequest = TestDataUtils.createItemRequestNoResponses();
        User owner = TestDataUtils.createOwner();

        ItemRequestWithResponsesDto dto = service.findItemRequest(itemRequest.getId(), owner.getId());

        TypedQuery<ItemRequest> query = em
                .createQuery("SELECT ir FROM ItemRequest ir " +
                        "WHERE ir.id = :itemRequestId", ItemRequest.class)
                .setParameter("itemRequestId", itemRequest.getId());
        ItemRequest result = query.getSingleResult();
        ItemRequestWithResponsesDto expected = ItemRequestMapper.mapToItemRequestWithResponsesDto(result);
        assertEquals(expected, dto);
    }
}