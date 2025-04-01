package ru.practicum.shareit.request.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.utils.TestDataUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestRepositoryTest {
    final ItemRequestRepository repository;

    @Test
    void findAllByOrderByCreatedDesc() {
        List<ItemRequest> itemRequests = List.of(
                TestDataUtils.createItemRequestNoResponses(),
                TestDataUtils.createItemRequest()
        );

        List<ItemRequest> expected = repository.findAllByOrderByCreatedDesc();

        assertEquals(expected, itemRequests);
        assertEquals(expected.getFirst(), itemRequests.getFirst());
        assertEquals(expected.getLast(), itemRequests.getLast());
    }
}