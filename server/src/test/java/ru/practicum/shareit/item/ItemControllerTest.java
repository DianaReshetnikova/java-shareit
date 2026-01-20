package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    private static final String PATH = "/items";

    @Autowired
    ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    ItemService itemService;

    private User user = new User(
            1L,
            "John",
            "john.doe@mail.com"
    );

    private ItemDto itemDto = ItemDto.builder()
            .name("name item")
            .description("description item")
            .available(true)
            .build();

    private Item item = Item.builder()
            .id(1L)
            .name(itemDto.getName())
            .description(itemDto.getDescription())
            .available(itemDto.getAvailable())
            .owner(user)
            .build();


    @Test
    void createItemTest() throws Exception {
        when(itemService.createItem(anyLong(), any(ItemDto.class)))
                .thenReturn(item);

        mvc.perform(post(PATH)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", Long.toString(user.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(item.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()))
                .andExpect(jsonPath("$.requestId").value(itemDto.getRequestId()));

        Mockito.verify(itemService, Mockito.times(1))
                .createItem(anyLong(), any(ItemDto.class));
    }

    @Test
    void updateItemByIdTest() throws Exception {
        when(itemService.updateItem(anyLong(), anyLong(), any(ItemDto.class)))
                .thenReturn(item);

        mvc.perform(patch(PATH + "/{itemId}", item.getId())
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", Long.toString(user.getId()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(item.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()))
                .andExpect(jsonPath("$.requestId").value(itemDto.getRequestId()));

        Mockito.verify(itemService, Mockito.times(1))
                .updateItem(anyLong(), anyLong(), any(ItemDto.class));
    }

    @Test
    void searchForItemsTest() throws Exception {
        Item item2 = item.toBuilder()
                .id(2L)
                .build();

        List<Item> items = List.of(item, item2);

        when(itemService.searchForItems(anyString()))
                .thenReturn(items);

        MvcResult result = mvc.perform(get(PATH + "/search")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .param("text", "text value")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<ItemDto> itemsDto = items.stream()
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.toList());

        assertEquals(mapper.writeValueAsString(itemsDto), result.getResponse().getContentAsString());

        Mockito.verify(itemService, Mockito.times(1))
                .searchForItems(anyString());
    }

    @Test
    void getItemByIdTest() throws Exception {
        ItemBookingDto itemBookingDto = ItemBookingDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .lastBooking(null)
                .nextBooking(null)
                .available(item.getAvailable())
                .comments(null)
                .build();

        when(itemService.getItemBookingDtoById(anyLong()))
                .thenReturn(itemBookingDto);

        mvc.perform(get(PATH + "/{itemId}", item.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemBookingDto.getId()))
                .andExpect(jsonPath("$.name").value(itemBookingDto.getName()))
                .andExpect(jsonPath("$.description").value(itemBookingDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemBookingDto.getAvailable()))
                .andExpect(jsonPath("$.lastBooking").value(itemBookingDto.getLastBooking()))
                .andExpect(jsonPath("$.nextBooking").value(itemBookingDto.getNextBooking()))
                .andExpect(jsonPath("$.comments").value(itemBookingDto.getComments()));

        Mockito.verify(itemService, Mockito.times(1))
                .getItemBookingDtoById(anyLong());
    }

    @Test
    void getItemsByOwnerTest() throws Exception {
        BookingShortDto lastBooking = BookingShortDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2025, 1, 1, 10, 10, 10))
                .end(LocalDateTime.of(2025, 1, 8, 10, 10, 10))
                .status(BookingStatus.APPROVED)
                .build();

        BookingShortDto nextBooking = BookingShortDto.builder()
                .id(2L)
                .start(LocalDateTime.of(2025, 3, 15, 10, 10, 10))
                .end(LocalDateTime.of(2025, 3, 30, 10, 10, 10))
                .status(BookingStatus.WAITING)
                .build();

        ItemBookingDto itemBookingDto = ItemBookingDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .available(item.getAvailable())
                .comments(null)
                .build();

        ItemBookingDto itemBookingDto2 = itemBookingDto.toBuilder()
                .id(2L)
                .build();

        List<ItemBookingDto> itemBookings = List.of(itemBookingDto, itemBookingDto2);

        when(itemService.getItemsByOwner(anyLong()))
                .thenReturn(itemBookings);

        MvcResult result = mvc.perform(get(PATH)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", Long.toString(user.getId()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(mapper.writeValueAsString(itemBookings), result.getResponse().getContentAsString());

        Mockito.verify(itemService, Mockito.times(1))
                .getItemsByOwner(anyLong());
    }
}