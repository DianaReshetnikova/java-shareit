package ru.practicum.shareit.user.service;

import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<User> getUsers();

    User createUser(UserDto userDto) throws DuplicateException, ValidationException;

    User updateUser(Long userId, UserDto userDto) throws NotFoundException, ValidationException, DuplicateException;

    User getUserById(Long id) throws NotFoundException;

    void deleteUserById(Long id);
}
