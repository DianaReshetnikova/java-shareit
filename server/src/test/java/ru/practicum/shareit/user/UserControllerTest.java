package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/*
 * Мы не проверяем логику внутри сервиса (сохранилось ли что-то в базу, обновились ли поля). Для этого существуют интеграционные тесты.
 * Проверяем только Контроллер:
 * Правильно ли он принимает HTTP-запрос.
 * Верно ли он передает аргументы в метод сервиса.
 * Корректно ли он превращает ответ от сервиса обратно в JSON
 * */

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {
    private static final String PATH = "/users";

    @Autowired
    ObjectMapper mapper;

    @MockBean
    UserService userService;

    @Autowired
    private MockMvc mvc;

    private UserDto userDto = new UserDto(
            "John",
            "john.doe@mail.com"
    );

    private User user = UserMapper.mapToUser(userDto, 1L);


    @Test
    void createUserTest() throws Exception {
        when(userService.createUser(any(UserDto.class)))
                .thenReturn(user);

        mvc.perform(post(PATH)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.name").value(user.getName()))
                .andExpect(jsonPath("$.email").value(user.getEmail()));

        Mockito.verify(userService, Mockito.times(1))
                .createUser(any(UserDto.class));
    }

    @Test
    void updateUserTest() throws Exception {
        when(userService.updateUser(anyLong(), any(UserDto.class)))
                .thenReturn(user);

        mvc.perform(patch(PATH + "/{userId}", user.getId())
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.name").value(user.getName()))
                .andExpect(jsonPath("$.email").value(user.getEmail()));

        Mockito.verify(userService, Mockito.times(1))
                .updateUser(anyLong(), any(UserDto.class));
    }

    @Test
    void deleteUserByIdTest() throws Exception {
        mvc.perform(delete(PATH + "/{userId}", user.getId()))
                .andExpect(status().isNoContent());

        Mockito.verify(userService, Mockito.times(1))
                .deleteUserById(anyLong());
    }

    @Test
    void getUserByIdTest() throws Exception {
        when(userService.getUserById(anyLong()))
                .thenReturn(user);

        mvc.perform(get(PATH + "/{userId}", user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.name").value(user.getName()))
                .andExpect(jsonPath("$.email").value(user.getEmail()));

        Mockito.verify(userService, Mockito.times(1))
                .getUserById(anyLong());
    }

    @Test
    void getUsersTest() throws Exception {
        User user2 = new User(2L, "User2", "user2@mail.com");
        var users = List.of(user, user2);

        when(userService.getUsers())
                .thenReturn(users);

        MvcResult result = mvc.perform(get(PATH)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(mapper.writeValueAsString(users), result.getResponse().getContentAsString());

        Mockito.verify(userService, Mockito.times(1))
                .getUsers();
    }
}
