package ru.practicum.shareit.request;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.QItem;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.dto.response.ItemRequestWithResponsesDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@Transactional(readOnly = true)
public class ItemRequestService {
    final ItemRequestRepository itemRequestRepository;
    final UserRepository userRepository;
    final EntityManager entityManager;

    @Autowired
    public ItemRequestService(ItemRequestRepository itemRequestRepository,
                              UserRepository userRepository,
                              EntityManager entityManager) {
        this.itemRequestRepository = itemRequestRepository;
        this.userRepository = userRepository;
        this.entityManager = entityManager;
    }

    @Transactional
    public ItemRequestDto createItemRequest(NewItemRequestDto newItemRequestDto, Long userId) {
        ItemRequest itemRequest = ItemRequestMapper.mapToItemRequest(newItemRequestDto, userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %d не найден", userId))));
        itemRequestRepository.save(itemRequest);
        return ItemRequestMapper.mapToItemRequestDto(itemRequest);
    }

    public List<ItemRequestWithResponsesDto> findAllItemRequestsForRequestor(Long userId) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        QItem qItem = QItem.item;
        QItemRequest qItemRequest = QItemRequest.itemRequest;

        List<ItemRequest> itemRequests;
        List<Item> items;
        if (userRepository.existsById(userId)) {
            itemRequests = queryFactory.selectFrom(qItemRequest)
                    .where(qItemRequest.requestor.id.eq(userId))
                    .orderBy(qItemRequest.created.desc())
                    .fetch();
            items = queryFactory.selectFrom(qItem)
                    .where(qItem.requestId.in(itemRequests
                            .stream()
                            .map(ItemRequest::getId)
                            .toList()))
                    .fetch();
        } else {
            throw new NotFoundException(String.format("Пользователь с id = %d не найден", userId));
        }

        List<ItemRequestWithResponsesDto> dtoList = itemRequests.stream()
                .map(ItemRequestMapper::mapToItemRequestWithResponsesDto)
                .toList();
        if (items.isEmpty()) {
            return dtoList;
        } else {
            return dtoList.stream()
                    .map(itemRequestWithResponsesDto -> {
                        items.forEach(item -> {
                            if (Objects.equals(item.getRequestId(), itemRequestWithResponsesDto.getId())) {
                                itemRequestWithResponsesDto.getItems()
                                        .add(ItemRequestMapper.mapToResponseDto(item));
                            }
                        });
                        return itemRequestWithResponsesDto;
                    })
                    .toList();
        }
    }

    public List<ItemRequestDto> findAllItemRequests(Long userId) {
        return ItemRequestMapper.mapToItemRequestDtoList(itemRequestRepository.findAllByOrderByCreatedDesc());
    }

    public ItemRequestWithResponsesDto findItemRequest(Long requestId, Long userId) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        QItem qItem = QItem.item;

        ItemRequest itemRequest;
        List<Item> items;
        if (userRepository.existsById(userId)) {
            itemRequest = itemRequestRepository.findById(requestId)
                    .orElseThrow(() -> new NotFoundException(
                            String.format("Запрос вещи с id = %d не найден", requestId)));
            items = queryFactory.selectFrom(qItem)
                    .where(qItem.requestId.eq(requestId))
                    .fetch();
        } else {
            throw new NotFoundException(String.format("Пользователь с id = %d не найден", userId));
        }

        ItemRequestWithResponsesDto dto = ItemRequestMapper.mapToItemRequestWithResponsesDto(itemRequest);
        if (!items.isEmpty()) {
            items.forEach(item -> {
                if (Objects.equals(item.getRequestId(), dto.getId())) {
                    dto.getItems().add(ItemRequestMapper.mapToResponseDto(item));
                }
            });
        }
        return dto;
    }
}
