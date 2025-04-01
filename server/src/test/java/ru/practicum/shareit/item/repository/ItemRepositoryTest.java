package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.utils.TestDataUtils;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRepositoryTest {
    final ItemRepository repository;

    @Test
    void search() {
        Collection<Item> items = List.of(TestDataUtils.createAvailableItem());

        Collection<Item> expected = repository.search(TestDataUtils.TEXT_SEARCH_ITEM);

        assertEquals(expected, items);
    }
}