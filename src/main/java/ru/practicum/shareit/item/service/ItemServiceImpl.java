package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public List<ItemDto> getItemsByOwner(Long userId) {
        validateUserById(userId);
        return itemRepository.getItemsByOwner(userId);
    }

    @Override
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        validateUserById(userId);
        validateItemData(itemDto);

        return itemRepository.createItem(userId, itemDto);
    }

    @Override
    public ItemDto updateItem(Long userId, long itemId, ItemDto itemDto) {
        validateUserById(userId);

        if (!itemRepository.isExistItemById(itemId))
            throw new NotFoundException("Вещь с id = " + itemId + " не найдена.");
        if (!itemRepository.isOwnerOfItem(userId, itemId))
            throw new ValidationException("Пользователь id = " + userId + " не владелец вещи id = " + itemId);

        return itemRepository.updateItem(itemId, itemDto);
    }

    @Override
    public ItemDto getItemById(long itemId) {
        return itemRepository.getItemById(itemId).orElseThrow(() -> new NotFoundException("Вещь с id = " + itemId + " не найдена."));
    }

    @Override
    public List<ItemDto> searchForItems(String text) {
        if (!StringUtils.hasText(text))
            return List.of();
        else
            return itemRepository.searchForItems(text);
    }


    private void validateUserById(Long userId) {
        if (userId == null || !userRepository.isExistUserById(userId))
            throw new NotFoundException("Пользователь с id = " + userId + " не найден.");
    }

    private void validateItemData(ItemDto itemDto) {
        if (!StringUtils.hasText(itemDto.getName())
                || !StringUtils.hasText(itemDto.getDescription())
                || itemDto.getAvailable() == null)
            throw new ValidationException("Некорректные входные данные.");
    }
}
