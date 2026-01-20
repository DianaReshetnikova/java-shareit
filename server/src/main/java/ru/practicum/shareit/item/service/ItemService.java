package ru.practicum.shareit.item.service;

import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    List<ItemBookingDto> getItemsByOwner(Long userId) throws NotFoundException;

    Item createItem(Long userId, ItemDto itemDto) throws NotFoundException, ValidationException;

    Item updateItem(Long userId, Long itemId, ItemDto itemDto) throws NotFoundException, ValidationException;

    ItemBookingDto getItemBookingDtoById(Long itemId) throws NotFoundException;

    Item getItemById(Long itemId) throws NotFoundException;

    List<Item> searchForItems(String text);

    Comment postComment(Long itemId, Long userId, CommentCreateDto commentCreate) throws NotFoundException, ValidationException;
}
