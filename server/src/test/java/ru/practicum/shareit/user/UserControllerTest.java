package ru.practicum.shareit.user;

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
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.utils.TestDataUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
class UserControllerTest {
    private static ObjectMapper objectMapper;
    @MockBean
    private UserService service;
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
    void createUser() throws Exception {
        NewUserRequest newUserRequest = TestDataUtils.createNewUserRequest();
        User user = TestDataUtils.createUserFromNewUserRequest();
        UserDto dto = UserMapper.mapToUserDto(user);

        when(service.createUser(newUserRequest))
                .thenReturn(dto);

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(newUserRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));

        verify(service, times(1))
                .createUser(newUserRequest);
    }

    @Test
    void findAllUsers() throws Exception {
        List<User> users = List.of(
                TestDataUtils.createUser(),
                TestDataUtils.createOwner(),
                TestDataUtils.createBookerAuthor(),
                TestDataUtils.createRequestor()
        );
        List<UserDto> dto = UserMapper.mapToUserDtoList(users);

        when(service.findAllUsers())
                .thenReturn(dto);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));

        verify(service, times(1))
                .findAllUsers();
    }

    @Test
    void findUser() throws Exception {
        User user = TestDataUtils.createUser();
        UserDto dto = UserMapper.mapToUserDto(user);

        when(service.findUser(user.getId()))
                .thenReturn(dto);

        mockMvc.perform(get("/users/{userId}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));

        verify(service, times(1))
                .findUser(user.getId());
    }

    @Test
    void updateUser() throws Exception {
        UpdateUserRequest updateUserRequest = TestDataUtils.createUpdateUserRequest();
        User user = TestDataUtils.createUser();
        user.setName(updateUserRequest.getName());
        user.setEmail(updateUserRequest.getEmail());
        UserDto dto = UserMapper.mapToUserDto(user);

        when(service.updateUser(updateUserRequest, user.getId()))
                .thenReturn(dto);

        mockMvc.perform(patch("/users/{userId}", user.getId())
                        .content(objectMapper.writeValueAsString(updateUserRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));

        verify(service, times(1))
                .updateUser(updateUserRequest, user.getId());
    }

    @Test
    void removeUser() throws Exception {
        User user = TestDataUtils.createUser();

        doNothing()
                .when(service)
                .removeUser(eq(user.getId()));

        mockMvc.perform(delete("/users/{userId}", user.getId()))
                .andExpect(status().isNoContent());

        verify(service, times(1))
                .removeUser(user.getId());
    }
}