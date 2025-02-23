package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.Comparator;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemService {
    final ItemRepository itemRepository;
    final UserRepository userRepository;

    @Autowired
    public ItemService(ItemRepository itemRepository,
                       UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    public ItemDto createItem(NewItemRequest newItemRequest, Long ownerId) {
        Item item = ItemMapper.mapToItem(newItemRequest, userRepository.findUser(ownerId).orElseThrow());
        itemRepository.createItem(item);
        return ItemMapper.mapToItemDto(item);
    }

    public Collection<ItemDto> findAllOwnerItems(Long ownerId) {
        return itemRepository.findAllOwnerItems(userRepository.findUser(ownerId).orElseThrow()).stream()
                .map(ItemMapper::mapToItemDto)
                .sorted(Comparator.comparing(ItemDto::getId))
                .toList();
    }

    public ItemDto findItem(Long itemId) {
        return itemRepository.findItem(itemId)
                .map(ItemMapper::mapToItemDto)
                .orElseThrow(() -> new NotFoundException(String.format("Вещь с id = %d не найдена", itemId)));
    }

    public ItemDto updateItem(UpdateItemRequest updateItemRequest,
                              Long itemId,
                              Long ownerId) {
        Item updatedItem = itemRepository.updateItem(updateItemRequest, itemId, ownerId);
        return ItemMapper.mapToItemDto(updatedItem);
    }

    public void removeItem(Long itemId,
                           Long ownerId) {
        itemRepository.removeItem(itemId, ownerId);
    }

    public Collection<ItemDto> searchItemByNameOrDescription(String text,
                                                             Long userId) {
        return itemRepository.searchItemByNameOrDescription(text, userId).stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }
}
