package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemResponseDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserService userService;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequest createItemRequest(Long userId, ItemRequestCreateDto createDto) throws NotFoundException, ValidationException {
        User user = userService.getUserById(userId);
        validateItemRequestCreateDto(createDto);

        return itemRequestRepository.save(ItemRequestMapper.mapToModel(createDto, user));
    }

    @Override
    public List<ItemRequestDto> getOwnItemRequests(Long userId) throws NotFoundException {
        User user = userService.getUserById(userId);

        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(userId);

        return itemRequests.stream()
                .map(this::getItemResponsesForItemRequest)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getOthersItemRequests(Long userId) throws NotFoundException {
        User user = userService.getUserById(userId);

        List<ItemRequest> itemsOtherRequest = itemRequestRepository.findAllByRequesterIdNotOrderByCreatedDesc(userId);

        return itemsOtherRequest.stream()
                .map(this::getItemResponsesForItemRequest)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getItemRequestById(Long requestId) throws NotFoundException {
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с id = " + requestId + " на вещь не найден."));

        return getItemResponsesForItemRequest(itemRequest);
    }


    private void validateItemRequestCreateDto(ItemRequestCreateDto createDto) throws ValidationException {
        if (!StringUtils.hasText(createDto.getDescription()))
            throw new ValidationException("Некорректные входные данные.");
    }

    private ItemRequestDto getItemResponsesForItemRequest(ItemRequest itemRequest) {
        List<Item> itemResponses = itemRepository.findAllByItemRequestId(itemRequest.getId());

        List<ItemResponseDto> itemsResponseDto;
        if (itemResponses == null)
            itemsResponseDto = Collections.emptyList();
        else
            itemsResponseDto = ItemRequestMapper.mapToItemResponseDtoList(itemResponses);

        return ItemRequestMapper.mapToDto(itemRequest, itemsResponseDto);
    }
}
