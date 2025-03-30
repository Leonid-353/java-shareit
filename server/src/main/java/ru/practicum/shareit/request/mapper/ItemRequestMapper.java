package ru.practicum.shareit.request.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.dto.response.ItemRequestWithResponsesDto;
import ru.practicum.shareit.request.dto.response.ResponseDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemRequestMapper {
    public static ItemRequest mapToItemRequest(NewItemRequestDto request, User requestor) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(request.getDescription());
        itemRequest.setRequestor(requestor);
        itemRequest.setCreated(request.getCreated());

        return itemRequest;
    }

    public static ItemRequestDto mapToItemRequestDto(ItemRequest itemRequest) {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(itemRequest.getId());
        dto.setDescription(itemRequest.getDescription());
        dto.setRequestor(UserMapper.mapToUserDto(itemRequest.getRequestor()));
        dto.setCreated(itemRequest.getCreated());

        return dto;
    }

    public static ResponseDto mapToResponseDto(Item item) {
        ResponseDto dto = new ResponseDto();
        dto.setItemId(item.getId());
        dto.setName(item.getName());
        dto.setOwnerId(item.getOwner().getId());

        return dto;
    }

    public static ItemRequestWithResponsesDto mapToItemRequestWithResponsesDto(ItemRequest itemRequest) {
        ItemRequestWithResponsesDto dto = new ItemRequestWithResponsesDto();
        dto.setId(itemRequest.getId());
        dto.setDescription(itemRequest.getDescription());
        dto.setRequestor(UserMapper.mapToUserDto(itemRequest.getRequestor()));
        dto.setCreated(itemRequest.getCreated());
        dto.setItems(new ArrayList<>());

        return dto;
    }

    public static List<ItemRequestWithResponsesDto> mapToItemRequestWithResponsesDtoList(
            List<ItemRequest> itemRequests) {
        return itemRequests.stream()
                .map(ItemRequestMapper::mapToItemRequestWithResponsesDto)
                .toList();
    }

    public static List<ItemRequestDto> mapToItemRequestDtoList(List<ItemRequest> itemRequests) {
        return itemRequests.stream()
                .map(ItemRequestMapper::mapToItemRequestDto)
                .toList();
    }
}
