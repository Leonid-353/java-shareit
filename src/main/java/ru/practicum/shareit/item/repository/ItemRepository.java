package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.user.User;

import java.util.Collection;
import java.util.Optional;

public interface ItemRepository {
    Item createItem(Item item);

    Collection<Item> findAllOwnerItems(User owner);

    Optional<Item> findItem(Long itemId);

    Item updateItem(UpdateItemRequest updateItemRequest, Long itemId, Long ownerId);

    void removeItem(Long itemId, Long ownerId);

    Collection<Item> searchItemByNameOrDescription(String text, Long userId);
}
