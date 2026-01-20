package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;


    private UserDto userDto = new UserDto(
            "John",
            "john.doe@mail.com"
    );

    private User user = UserMapper.mapToUser(userDto, 1L);

    @Test
    void createUserTest() throws Exception {
        when(userRepository.existsByEmail(anyString()))
                .thenReturn(false);

        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        User createdUser = userService.createUser(userDto);
        assertEquals(user.getId(), createdUser.getId());
        assertEquals(user.getName(), createdUser.getName());
        assertEquals(user.getEmail(), createdUser.getEmail());

        Mockito.verify(userRepository, times(1))
                .existsByEmail(anyString());
        Mockito.verify(userRepository, times(1))
                .save(any(User.class));
    }

    @Test
    void updateUserTest_whenCorrectDataAndUserExists() throws Exception {
        User user2 = user.toBuilder()
                .name("User 1 Updated")
                .build();

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        when(userRepository.existsByEmailAndIdNot(anyString(), anyLong()))
                .thenReturn(false);

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0, User.class));

        User updatedUser = userService.updateUser(user.getId(), UserMapper.mapToUserDto(user2));
        assertEquals(user2.getId(), updatedUser.getId());
        assertEquals(user2.getName(), updatedUser.getName());
        assertEquals(user2.getEmail(), updatedUser.getEmail());

        Mockito.verify(userRepository, times(1))
                .findById(anyLong());
        Mockito.verify(userRepository, times(1))
                .existsByEmailAndIdNot(anyString(), anyLong());
        Mockito.verify(userRepository, times(1))
                .save(any(User.class));
    }

    @Test
    void updateUserTest_whenUserNotExists() throws Exception {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.updateUser(user.getId(), userDto));

        Mockito.verify(userRepository, times(1))
                .findById(anyLong());
        Mockito.verify(userRepository, never())
                .existsByEmailAndIdNot(anyString(), anyLong());
        Mockito.verify(userRepository, never())
                .save(any(User.class));
    }

    @Test
    void updateUserTest_whenUserEmailAlreadyExists() throws Exception {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(userRepository.existsByEmailAndIdNot(anyString(), anyLong()))
                .thenReturn(true);

        assertThrows(DuplicateException.class, () -> userService.updateUser(user.getId(), userDto));

        Mockito.verify(userRepository, times(1))
                .findById(anyLong());
        Mockito.verify(userRepository, times(1))
                .existsByEmailAndIdNot(anyString(),anyLong());
        Mockito.verify(userRepository, never())
                .save(any(User.class));
    }

    @Test
    void getUserByIdTest_whenUserExists() throws Exception {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        User findedUser = userService.getUserById(user.getId());
        assertEquals(user.getId(), findedUser.getId());
        assertEquals(user.getName(), findedUser.getName());
        assertEquals(user.getEmail(), findedUser.getEmail());

        Mockito.verify(userRepository, times(1))
                .findById(anyLong());
    }

    @Test
    void getUserByIdTest_whenUserNotExists() throws Exception {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUserById(user.getId()));

        Mockito.verify(userRepository, times(1))
                .findById(anyLong());
    }

    @Test
    void deleteUserByIdTest() {
        userService.deleteUserById(user.getId());

        Mockito.verify(userRepository, times(1))
                .deleteById(anyLong());
    }

    @Test
    void getUsersTest() throws Exception {
        User user2 = user.toBuilder()
                .id(2L)
                .email("user2@mail.com")
                .build();
        List<User> usersList = List.of(user, user2);

        when(userRepository.findAll())
                .thenReturn(usersList);

        var result = userService.getUsers();
        assertThat(result)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactlyElementsOf(usersList);

        Mockito.verify(userRepository, times(1))
                .findAll();
    }
}
