package ru.practicum.shareit.user.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class UserRepositoryInMemory implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public List<User> getUsers() {
        return users.values().stream()
                .toList();
    }

    @Override
    public User createUser(UserDto userDto) {
        userDto.setId(getId());
        User user = UserMapper.mapToUser(userDto);
        users.put(user.getId(), user);

        return user;
    }

    @Override
    public User updateUser(Long userId, UserDto userDto) {
        User user = users.get(userId);

        if (userDto.getName() != null)
            user.setName(userDto.getName());
        if (userDto.getEmail() != null)
            user.setEmail(userDto.getEmail());

        users.replace(userId, user);
        return user;
    }

    @Override
    public Optional<User> getUserById(Long userid) {
        return Optional.of(users.get(userid));
    }

    @Override
    public void deleteUserById(Long userId) {
        users.remove(userId);
    }

    @Override
    public boolean isExistEmail(String email) {
        return users.values().stream()
                .anyMatch(user -> user.getEmail().equals(email));
    }

    @Override
    public boolean isExistUserById(Long userid) {
        return users.keySet().stream()
                .anyMatch(id -> id.equals(userid));
    }

    private Long getId() {
        long id = users.keySet().stream()
                .mapToLong(aLong -> aLong)
                .max()
                .orElse(0);

        return ++id;
    }
}
