package ru.practicum.shareit.item.service;

import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    List<ItemDto> getItemsByOwner(Long userId) throws NotFoundException;

    ItemDto createItem(Long userId, ItemDto itemDto) throws NotFoundException, ValidationException;

    ItemDto updateItem(Long userId, long itemId, ItemDto itemDto) throws NotFoundException, ValidationException;

    ItemDto getItemById(long itemId) throws NotFoundException;

    List<ItemDto> searchForItems(String text);
}
