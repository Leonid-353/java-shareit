package ru.practicum.shareit.user.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.utils.TestDataUtils;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserRepositoryTest {
    final UserRepository repository;

    @Test
    void existsUserByEmail() {
        UpdateUserRequest updateUserRequest = TestDataUtils.createUpdateUserRequestDuplicateEmail();

        assertTrue(repository.existsUserByEmail(updateUserRequest.getEmail()));
    }
}