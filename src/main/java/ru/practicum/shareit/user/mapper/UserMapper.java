package ru.practicum.shareit.user.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {
    public static User mapToUser(NewUserRequest request) {
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());

        return user;
    }

    public static UserDto mapToUserDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());

        return dto;
    }

    public static User updateUserFields(User user, UpdateUserRequest request) {
        try {
            if (request.hasName() && !(request.getName().equals(user.getName()))) {
                user.setName(request.getName());
            }
            if (request.hasEmail() && !(request.getEmail().equals(user.getEmail()))) {
                user.setEmail(request.getEmail());
            }
        } catch (RuntimeException e) {
            throw new DuplicatedDataException("Этот email уже используется", e);
        }


        return user;
    }
}
