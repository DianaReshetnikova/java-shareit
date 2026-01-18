package ru.practicum.shareit.request.service;

import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequest createItemRequest(Long userId, ItemRequestCreateDto createDto) throws NotFoundException, ValidationException;

    List<ItemRequestDto> getOwnItemRequests(Long userId) throws NotFoundException;

    List<ItemRequestDto> getOthersItemRequests(Long userId) throws NotFoundException;

    ItemRequestDto getItemRequestById(Long requestId) throws NotFoundException;
}
