package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<User> getUsers() {
        return userRepository.getUsers();
    }

    @Override
    public User createUser(UserDto userDto) {
        validateUserData(userDto);
        return userRepository.createUser(userDto);
    }

    @Override
    public User updateUser(Long userId, UserDto userDto) {
        validateUserDataForUpdate(userDto, userId);
        return userRepository.updateUser(userId, userDto);
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.getUserById(id).orElseThrow(() -> new NotFoundException("Пользователь с " + id + " не найден"));
    }

    @Override
    public void deleteUserById(Long id) {
        userRepository.deleteUserById(id);
    }

    private void validateUserData(UserDto userDto) throws DuplicateException, ValidationException {
        // true, если строка не равна null, её длина больше 0 и она содержит хотя бы один непустой символ.
        if (!StringUtils.hasText(userDto.getName()) || !StringUtils.hasText(userDto.getEmail())) {
            throw new ValidationException("Поля name или email не заполнены.");
        }
        if (userRepository.isExistEmail(userDto.getEmail())) {
            throw new DuplicateException("Пользователь с email = " + userDto.getEmail() + " уже существует.");
        }
        if (!userDto.getEmail().contains("@")) {
            throw new ValidationException("Указан некорректный email = " + userDto.getEmail() + ".");
        }
    }

    private void validateUserDataForUpdate(UserDto userDto, long userId) {
        if (!userRepository.isExistUserById(userId))
            throw new NotFoundException("Пользователь с id = " + userId + " не существует.");

        if (StringUtils.hasText(userDto.getEmail()) && !userDto.getEmail().contains("@")) {
            throw new ValidationException("Указан некорректный email = " + userDto.getEmail() + ".");
        }
        if (StringUtils.hasText(userDto.getEmail()) && userRepository.isExistEmail(userDto.getEmail())) {
            throw new DuplicateException("Пользователь с email = " + userDto.getEmail() + " уже существует.");
        }
    }
}
