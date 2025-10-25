package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<User> getUsers();

    User createUser(UserDto userDto);

    User updateUser(Long userId, UserDto userDto);

    User getUserById(Long id);

    void deleteUserById(Long id);
}
