package ru.practicum.shareit.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.utils.BaseTestServiceContext;
import ru.practicum.shareit.utils.TestDataUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceTest extends BaseTestServiceContext {
    private final EntityManager em;
    private final UserService service;

    // Create user
    @Test
    void createUserSuccessful() {
        NewUserRequest newUserRequest = TestDataUtils.createNewUserRequest();

        UserDto dto = service.createUser(newUserRequest);

        TypedQuery<User> query = em
                .createQuery("SELECT u FROM User u " +
                        "WHERE u.id = :userId", User.class);
        query.setParameter("userId", dto.getId());
        User result = query.getSingleResult();
        UserDto expected = UserMapper.mapToUserDto(result);
        assertEquals(expected, dto);
    }

    @Test
    void createUserNotUniqueEmail() {
        NewUserRequest newUserRequest = TestDataUtils.createNewUserRequestNotUniqueEmail();

        assertThrows(Exception.class, () -> service.createUser(newUserRequest));
    }

    // Find all users
    @Test
    void findAllUsers() {
        User user = TestDataUtils.createUser();

        List<UserDto> users = service.findAllUsers();

        assertEquals(UserMapper.mapToUserDto(user), users.getFirst());

        TypedQuery<User> query = em
                .createQuery("SELECT u FROM User u", User.class);
        List<User> resultList = query.getResultList();
        List<UserDto> expected = UserMapper.mapToUserDtoList(resultList);
        assertEquals(expected, users);
        assertEquals(4, users.size());
    }

    // Find user
    @Test
    void findUser() {
        User user = TestDataUtils.createUser();

        UserDto dto = service.findUser(user.getId());

        assertEquals(UserMapper.mapToUserDto(user), dto);

        TypedQuery<User> query = em
                .createQuery("SELECT u FROM User u " +
                        "WHERE u.id = :userId", User.class);
        query.setParameter("userId", user.getId());
        User result = query.getSingleResult();
        UserDto expected = UserMapper.mapToUserDto(result);
        assertEquals(expected, dto);
    }

    @Test
    void findUserNotFound() {
        assertThrows(NotFoundException.class, () -> service.findUser(Long.MAX_VALUE));
    }

    // Update user
    @Test
    void updateUserSuccessful() {
        UpdateUserRequest updateUserRequest = TestDataUtils.createUpdateUserRequest();
        User user = TestDataUtils.createUser();

        UserDto dto = service.updateUser(updateUserRequest, user.getId());

        TypedQuery<User> query = em
                .createQuery("SELECT u FROM User u " +
                        "WHERE u.id = :userId", User.class);
        query.setParameter("userId", user.getId());
        User result = query.getSingleResult();
        UserDto expected = UserMapper.mapToUserDto(result);
        assertEquals(expected, dto);
    }

    @Test
    void updateUserNotFound() {
        UpdateUserRequest updateUserRequest = TestDataUtils.createUpdateUserRequest();

        assertThrows(NotFoundException.class, () -> service.updateUser(updateUserRequest, Long.MAX_VALUE));
    }

    // Remove user
    @Test
    void removeUser() {
        User user = TestDataUtils.createUser();

        Optional<UserDto> existingUserBeforeDeletion = Optional.of(service.findUser(user.getId()));
        assertTrue(existingUserBeforeDeletion.isPresent());

        service.removeUser(user.getId());

        assertThrows(NotFoundException.class, () -> service.findUser(user.getId()));

        TypedQuery<User> query = em
                .createQuery("SELECT u FROM User u " +
                        "WHERE u.id = :userId", User.class)
                .setParameter("userId", user.getId());
        List<User> result = query.getResultList();
        assertTrue(result.isEmpty());
    }
}