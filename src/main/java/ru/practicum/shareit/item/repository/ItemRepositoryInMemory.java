package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ItemRepositoryInMemory implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public List<ItemDto> getItemsByOwner(long userId) {
        return items.values().stream()
                .filter(item -> item.getOwnerId().equals(userId))
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    @Override
    public ItemDto createItem(long userId, ItemDto itemDto) {
        itemDto.setId(getId());
        Item item = ItemMapper.mapToItem(itemDto, userId);

        items.put(item.getId(), item);
        return itemDto;
    }

    @Override
    public ItemDto updateItem(long itemId, ItemDto itemDto) {
        Item item = items.get(itemId);

        if (itemDto.getName() != null)
            item.setName(itemDto.getName());
        if (itemDto.getDescription() != null)
            item.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null)
            item.setAvailable(itemDto.getAvailable());

        items.replace(itemId, item);
        return itemDto;
    }

    @Override
    public Optional<ItemDto> getItemById(long itemId) {
        Item item = items.get(itemId);
        if (item == null)
            return Optional.empty();
        else
            return Optional.of(ItemMapper.mapToItemDto(item));
    }

    @Override
    public List<ItemDto> searchForItems(String text) {
        String textLowerCase = text.toLowerCase();

        return items.values().stream()
                .filter(item -> item.getName().toLowerCase().contains(textLowerCase) ||
                        item.getDescription().toLowerCase().contains(textLowerCase))
                .filter(item -> item.getAvailable() == true)
                .map(ItemMapper::mapToItemDto)
                .toList();
    }


    @Override
    public boolean isExistItemById(Long itemId) {
        return items.get(itemId) != null;
    }

    @Override
    public boolean isOwnerOfItem(long userId, long itemId) {
        Item item = items.get(itemId);
        if (item != null)
            return item.getOwnerId().equals(userId);

        return false;
    }


    private Long getId() {
        long id = items.keySet().stream()
                .mapToLong(aLong -> aLong)
                .max()
                .orElse(0);

        return ++id;
    }
}
