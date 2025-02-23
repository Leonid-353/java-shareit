package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ForbiddenOperationException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.User;

import java.util.*;

@Slf4j
@Repository
public class InMemoryItemRepository implements ItemRepository {

    Map<Long, Item> items = new HashMap<>();

    @Override
    public Item createItem(Item item) {
        item.setId(getNextId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Collection<Item> findAllOwnerItems(User owner) {
        return items.values().stream()
                .filter(item -> Objects.equals(item.getOwner(), owner))
                .toList();
    }

    @Override
    public Optional<Item> findItem(Long itemId) {
        if (!items.containsKey(itemId)) {
            throw new NotFoundException(String.format("Вещь с id = %d не найдена", itemId));
        }
        return Optional.of(items.get(itemId));
    }

    @Override
    public Item updateItem(UpdateItemRequest updateItemRequest,
                           Long itemId,
                           Long ownerId) {
        if (!Objects.equals(items.get(itemId).getOwner().getId(), ownerId)) {
            throw new ForbiddenOperationException("Изменять вещь может только её владелец");
        }
        return findItem(itemId)
                .map(item -> ItemMapper.updateItemFields(item, updateItemRequest))
                .orElseThrow();
    }

    @Override
    public void removeItem(Long itemId,
                           Long ownerId) {
        if (!Objects.equals(items.get(itemId).getOwner().getId(), ownerId)) {
            throw new ForbiddenOperationException("Удалять вещь может только её владелец");
        }
        items.remove(itemId);
    }

    @Override
    public Collection<Item> searchItemByNameOrDescription(String text,
                                                          Long userId) {
        List<Item> foundItems = new ArrayList<>();

        if (!text.isBlank() && !items.isEmpty()) {
            String normalizedText = text.trim().toLowerCase();
            for (Map.Entry<Long, Item> entry : items.entrySet()) {
                Item item = entry.getValue();

                if (item.isAvailable()) {
                    String normalizedName = item.getName().trim().toLowerCase();
                    String normalizedDescription = item.getDescription().trim().toLowerCase();

                    if (normalizedName.contains(normalizedText)) {
                        log.info("Элемент найден по имени: {}", text);
                        foundItems.add(item);
                    } else if (normalizedDescription.contains(normalizedText)) {
                        log.info("Элемент найден по описанию: {}", text);
                        foundItems.add(item);
                    }
                }
            }
        }
        return foundItems;
    }

    private Long getNextId() {
        long currentMaxId = items.keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
