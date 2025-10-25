package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    List<User> getUsers();

    User createUser(UserDto userDto);

    User updateUser(Long userId, UserDto userDto);

    Optional<User> getUserById(Long userid);

    void deleteUserById(Long userId);

    boolean isExistEmail(String email);

    boolean isExistUserById(Long userid);
}
