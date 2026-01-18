package ru.practicum.shareit.request.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemResponseDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ItemRequestMapper {

    public static ItemRequest mapToModel(ItemRequestCreateDto createDto, User user) {
        return ItemRequest.builder()
                .requester(user)
                .created(LocalDateTime.now())
                .description(createDto.getDescription())
                .build();
    }

    public static ItemRequestDto mapToDto(ItemRequest itemRequest, List<ItemResponseDto> itemResponses) {
        ItemRequestDto itemRequestDto = mapToDto(itemRequest);
        itemRequestDto.setItems(itemResponses);
        return itemRequestDto;
    }

    public static ItemRequestDto mapToDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .requesterId(itemRequest.getRequester().getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(Collections.emptyList())
                .build();
    }

    public static List<ItemResponseDto> mapToItemResponseDtoList(List<Item> items) {
        return items.stream()
                .map(ItemRequestMapper::mapToItemResponseDto)
                .collect(Collectors.toList());
    }


    private static ItemResponseDto mapToItemResponseDto(Item item) {
        return ItemResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .ownerId(item.getOwner().getId())
                .build();
    }
}
