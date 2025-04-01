package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.constant.Constants;
import ru.practicum.shareit.item.dto.ItemCommentsDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.dto.comment.NewCommentRequest;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.mapper.comment.CommentMapper;
import ru.practicum.shareit.item.model.commet.Comment;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.utils.TestDataUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
class ItemControllerTest {
    private static ObjectMapper objectMapper;
    @MockBean
    private ItemService service;
    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    public static void setupObjectMapper() {
        objectMapper = new ObjectMapper();

        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(
                LocalDateTime.class,
                new LocalDateTimeSerializer(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        );
        javaTimeModule.addDeserializer(
                LocalDateTime.class,
                new LocalDateTimeDeserializer(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        );
        objectMapper.registerModule(javaTimeModule);
    }

    @Test
    void createItem() throws Exception {
        NewItemRequest newItemRequest = TestDataUtils.createNewItemRequest();
        Item item = TestDataUtils.createAvailableItemFromNewItemRequest();
        User owner = item.getOwner();
        ItemDto dto = ItemMapper.mapToItemDto(item);

        when(service.createItem(newItemRequest, owner.getId()))
                .thenReturn(dto);

        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(newItemRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(Constants.X_SHARER_USER_ID, owner.getId()))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));

        verify(service, times(1))
                .createItem(newItemRequest, owner.getId());
    }

    @Test
    void findAllOwnerItems() throws Exception {
        List<Item> items = List.of(
                TestDataUtils.createAvailableItem(),
                TestDataUtils.createNotAvailableItem()
        );
        User owner = TestDataUtils.createOwner();
        List<ItemCommentsDto> dto = ItemMapper.mapToItemCommentsDtoList(items);

        when(service.findAllOwnerItems(owner.getId()))
                .thenReturn(dto);

        mockMvc.perform(get("/items")
                        .header(Constants.X_SHARER_USER_ID, owner.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));

        verify(service, times(1))
                .findAllOwnerItems(owner.getId());
    }

    @Test
    void findItem() throws Exception {
        Item item = TestDataUtils.createAvailableItem();
        User owner = item.getOwner();
        ItemCommentsDto dto = ItemMapper.mapToItemCommentsDto(item);

        when(service.findItem(item.getId(), owner.getId()))
                .thenReturn(dto);

        mockMvc.perform(get("/items/{itemId}", item.getId())
                        .header(Constants.X_SHARER_USER_ID, owner.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));

        verify(service, times(1))
                .findItem(item.getId(), owner.getId());
    }

    @Test
    void updateItem() throws Exception {
        UpdateItemRequest updateItemRequest = TestDataUtils.createUpdateItemRequest();
        Item item = TestDataUtils.createNotAvailableItem();
        item.setAvailable(true);
        User owner = item.getOwner();
        ItemDto dto = ItemMapper.mapToItemDto(item);

        when(service.updateItem(updateItemRequest, item.getId(), owner.getId()))
                .thenReturn(dto);

        mockMvc.perform(patch("/items/{itemId}", item.getId())
                        .content(objectMapper.writeValueAsString(updateItemRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(Constants.X_SHARER_USER_ID, owner.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));

        verify(service, times(1))
                .updateItem(updateItemRequest, item.getId(), owner.getId());
    }

    @Test
    void removeItem() throws Exception {
        Item item = TestDataUtils.createAvailableItem();
        User owner = item.getOwner();

        doNothing()
                .when(service)
                .removeItem(eq(item.getId()), eq(owner.getId()));

        mockMvc.perform(delete("/items/{itemId}", item.getId())
                        .header(Constants.X_SHARER_USER_ID, owner.getId()))
                .andExpect(status().isNoContent());

        verify(service, times(1))
                .removeItem(item.getId(), owner.getId());
    }

    @Test
    void searchItemByNameOrDescription() throws Exception {
        List<Item> items = List.of(TestDataUtils.createAvailableItem());
        User user = TestDataUtils.createUser();
        List<ItemDto> dto = ItemMapper.mapToItemDtoList(items);

        when(service.searchItemByNameOrDescription(TestDataUtils.TEXT_SEARCH_ITEM, user.getId()))
                .thenReturn(dto);

        mockMvc.perform(get("/items/search")
                        .header(Constants.X_SHARER_USER_ID, user.getId())
                        .param("text", TestDataUtils.TEXT_SEARCH_ITEM))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));

        verify(service, times(1))
                .searchItemByNameOrDescription(TestDataUtils.TEXT_SEARCH_ITEM, user.getId());
    }

    @Test
    void createdComment() throws Exception {
        NewCommentRequest newCommentRequest = TestDataUtils.createNewCommentRequest();
        Comment comment = TestDataUtils.createCommentFromNewCommentRequest();
        Item item = comment.getItem();
        User author = comment.getAuthor();
        CommentDto dto = CommentMapper.mapToCommentDto(comment);

        when(service.createComment(newCommentRequest, item.getId(), author.getId()))
                .thenReturn(dto);

        mockMvc.perform(post("/items/{itemId}/comment", item.getId())
                        .content(objectMapper.writeValueAsString(newCommentRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(Constants.X_SHARER_USER_ID, author.getId()))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));

        verify(service, times(1))
                .createComment(newCommentRequest, item.getId(), author.getId());
    }
}