package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemBookingDto> getItemsByOwner(@RequestHeader("X-Sharer-User-Id") long userId) throws NotFoundException {
        return itemService.getItemsByOwner(userId);
    }

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody ItemDto itemDto) throws NotFoundException, ValidationException {
        return ItemMapper.mapToItemDto(itemService.createItem(userId, itemDto));
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItemById(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody ItemDto itemDto) throws NotFoundException, ValidationException {
        return ItemMapper.mapToItemDto(itemService.updateItem(userId, itemId, itemDto));
    }

    @GetMapping("/{itemId}")
    public ItemBookingDto getItemById(@PathVariable Long itemId) throws NotFoundException {
        return itemService.getItemBookingDtoById(itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchForItems(@RequestParam String text) {
        return itemService.searchForItems(text).stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    //Пользователи могут оставлять отзывы на те вещи, которые брали в аренду
    @PostMapping("/{itemId}/comment")
    public CommentResponseDto postComment(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody CommentCreateDto commentCreate) throws ValidationException, NotFoundException {
        return CommentMapper.mapToCommentResponseDto(itemService.postComment(itemId, userId, commentCreate));
    }
}
