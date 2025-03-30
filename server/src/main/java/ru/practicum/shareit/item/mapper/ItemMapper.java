package ru.practicum.shareit.item.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemCommentsDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.mapper.comment.CommentMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {
    public static Item mapToItem(NewItemRequest request, User owner) {
        Item item = new Item();
        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setAvailable(request.getAvailable());
        item.setOwner(owner);
        item.setRequestId(request.getRequestId() != null ? request.getRequestId() : null);

        return item;
    }

    public static ItemDto mapToItemDto(Item item) {
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setOwner(UserMapper.mapToUserDto(item.getOwner()));
        dto.setAvailable(item.isAvailable());
        dto.setLastBooking(item.getLastBooking());
        dto.setNextBooking(item.getNextBooking());
        dto.setRequest(item.getRequestId() != null ? item.getRequestId() : null);

        return dto;
    }

    public static List<ItemDto> mapToItemDtoList(List<Item> items) {
        return items.stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    public static ItemCommentsDto mapToItemCommentsDto(Item item) {
        ItemCommentsDto dto = new ItemCommentsDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setOwner(UserMapper.mapToUserDto(item.getOwner()));
        dto.setAvailable(item.isAvailable());
        dto.setLastBooking(item.getLastBooking());
        dto.setNextBooking(item.getNextBooking());
        dto.setComments(CommentMapper.mapToCommentDtoList(item.getComments()));
        dto.setRequest(item.getRequestId() != null ? item.getRequestId() : null);

        return dto;
    }

    public static List<ItemCommentsDto> mapToItemCommentsDtoList(List<Item> items) {
        return items.stream()
                .map(ItemMapper::mapToItemCommentsDto)
                .toList();
    }

    public static Item updateItemFields(Item item, UpdateItemRequest request) {
        if (request.hasName() && !(request.getName().equals(item.getName()))) {
            item.setName(request.getName());
        }
        if (request.hasDescription() && !(request.getDescription().equals(item.getDescription()))) {
            item.setDescription(request.getDescription());
        }
        if (request.hasAvailable()) {
            item.setAvailable(request.getAvailable());
        }

        return item;
    }
}
