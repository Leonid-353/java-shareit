package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.dto.response.ItemRequestWithResponsesDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.utils.TestDataUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
@AutoConfigureMockMvc
class ItemRequestControllerTest {
    private static ObjectMapper objectMapper;
    @MockBean
    private ItemRequestService service;
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
    void createItemRequest() throws Exception {
        NewItemRequestDto newItemRequestDto = TestDataUtils.createNewItemRequestDto();
        ItemRequest itemRequest = TestDataUtils.createItemRequestFromNewItemRequestDto();
        User requestor = itemRequest.getRequestor();
        ItemRequestDto dto = ItemRequestMapper.mapToItemRequestDto(itemRequest);

        when(service.createItemRequest(newItemRequestDto, requestor.getId()))
                .thenReturn(dto);

        mockMvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(newItemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(Constants.X_SHARER_USER_ID, requestor.getId()))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));

        verify(service, times(1))
                .createItemRequest(newItemRequestDto, requestor.getId());
    }

    @Test
    void findAllItemRequestsForRequestor() throws Exception {
        ItemRequest itemRequest = TestDataUtils.createItemRequest();
        User requestor = itemRequest.getRequestor();
        ItemRequestWithResponsesDto dto = ItemRequestMapper.mapToItemRequestWithResponsesDto(itemRequest);

        when(service.findAllItemRequestsForRequestor(requestor.getId()))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/requests")
                        .header(Constants.X_SHARER_USER_ID, requestor.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(dto))));

        verify(service, times(1))
                .findAllItemRequestsForRequestor(requestor.getId());
    }

    @Test
    void findAllItemRequests() throws Exception {
        List<ItemRequest> itemRequests = List.of(
                TestDataUtils.createItemRequest(),
                TestDataUtils.createItemRequestNoResponses()
        );
        User requestor = TestDataUtils.createRequestor();
        List<ItemRequestDto> dto = ItemRequestMapper.mapToItemRequestDtoList(itemRequests);

        when(service.findAllItemRequests(requestor.getId()))
                .thenReturn(dto);

        mockMvc.perform(get("/requests/all")
                        .header(Constants.X_SHARER_USER_ID, requestor.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));

        verify(service, times(1))
                .findAllItemRequests(requestor.getId());
    }

    @Test
    void findItemRequest() throws Exception {
        ItemRequest itemRequest = TestDataUtils.createItemRequest();
        User requestor = itemRequest.getRequestor();
        ItemRequestWithResponsesDto dto = ItemRequestMapper.mapToItemRequestWithResponsesDto(itemRequest);

        when(service.findItemRequest(itemRequest.getId(), requestor.getId()))
                .thenReturn(dto);

        mockMvc.perform(get("/requests/{requestId}", itemRequest.getId())
                        .header(Constants.X_SHARER_USER_ID, requestor.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));

        verify(service, times(1))
                .findItemRequest(itemRequest.getId(), requestor.getId());
    }
}