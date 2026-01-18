package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {
    private final static String PATH = "/requests";

    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemRequestService itemRequestService;

    @Autowired
    MockMvc mvc;

    private User user = new User(
            1L,
            "John",
            "john.doe@mail.com"
    );

    private ItemRequestCreateDto itemRequestCreateDto = new ItemRequestCreateDto("description text");
    private ItemRequest itemRequest = new ItemRequest(
            1L,
            itemRequestCreateDto.getDescription(),
            user,
            LocalDateTime.of(2026, 1, 18, 10, 30, 10)
    );
    private ItemRequestDto itemRequestDto = new ItemRequestDto(
            itemRequest.getId(),
            itemRequest.getRequester().getId(),
            itemRequest.getDescription(),
            itemRequest.getCreated(),
            new ArrayList<>()
    );

    @Test
    void createItemRequestTest() throws Exception {
        when(itemRequestService.createItemRequest(anyLong(), any(ItemRequestCreateDto.class)))
                .thenReturn(itemRequest);

        mvc.perform(post(PATH)
                        .content(mapper.writeValueAsString(itemRequestCreateDto))
                        .header("X-Sharer-User-Id", Long.toString(user.getId()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(itemRequestDto.getId()))
                .andExpect(jsonPath("$.description").value(itemRequestDto.getDescription()))
                .andExpect(jsonPath("$.requesterId").value(itemRequestDto.getRequesterId()))
                .andExpect(jsonPath("$.created").value(itemRequestDto.getCreated().toString()));

        Mockito.verify(itemRequestService, Mockito.times(1))
                .createItemRequest(anyLong(), any(ItemRequestCreateDto.class));
    }

    @Test
    void getOwnItemRequestsTest() throws Exception {
        ItemRequestDto itemRequestDto2 = new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getRequester().getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated(),
                new ArrayList<>()
        );

        List<ItemRequestDto> items = List.of(itemRequestDto, itemRequestDto2);

        when(itemRequestService.getOwnItemRequests(anyLong()))
                .thenReturn(items);

        MvcResult result = mvc.perform(get(PATH)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", Long.toString(user.getId()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(mapper.writeValueAsString(items), result.getResponse().getContentAsString());

        Mockito.verify(itemRequestService, Mockito.times(1))
                .getOwnItemRequests(anyLong());
    }

    @Test
    void getOthersItemRequestsTest() throws Exception {
        ItemRequestDto itemRequestDto2 = new ItemRequestDto(
                2L,
                55L,
                itemRequest.getDescription(),
                itemRequest.getCreated(),
                new ArrayList<>()
        );

        List<ItemRequestDto> items = List.of(itemRequestDto2);

        when(itemRequestService.getOthersItemRequests(anyLong()))
                .thenReturn(items);

        MvcResult result = mvc.perform(get(PATH + "/all")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", Long.toString(user.getId())))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(mapper.writeValueAsString(items), result.getResponse().getContentAsString());

        Mockito.verify(itemRequestService, Mockito.times(1))
                .getOthersItemRequests(anyLong());
    }

    @Test
    void getItemRequestByIdTest() throws Exception {
        when(itemRequestService.getItemRequestById(anyLong()))
                .thenReturn(itemRequestDto);

        mvc.perform(get(PATH + "/{requestId}", itemRequestDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequestDto.getId()))
                .andExpect(jsonPath("$.requesterId").value(itemRequestDto.getRequesterId()))
                .andExpect(jsonPath("$.description").value(itemRequestDto.getDescription()))
                .andExpect(jsonPath("$.created").value(itemRequestDto.getCreated().toString()))
                .andExpect(jsonPath("$.items").value(itemRequestDto.getItems()));

        Mockito.verify(itemRequestService, Mockito.times(1))
                .getItemRequestById(anyLong());
    }
}
