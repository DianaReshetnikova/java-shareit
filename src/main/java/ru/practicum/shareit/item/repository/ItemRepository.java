package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    List<ItemDto> getItemsByOwner(long userId);

    ItemDto createItem(long userId, ItemDto itemDto);

    ItemDto updateItem(long itemId, ItemDto itemDto);

    Optional<ItemDto> getItemById(long itemId);

    List<ItemDto> searchForItems(String text);

    boolean isExistItemById(Long itemId);

    boolean isOwnerOfItem(long userId, long itemId);
}
