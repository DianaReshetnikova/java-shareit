package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public List<ItemBookingDto> getItemsByOwner(Long userId) throws NotFoundException {
        validateUserById(userId);

        List<ItemBookingDto> listItemBookings = itemRepository.findAllByOwnerId(userId)
                .stream()
                .map(ItemMapper::mapToItemBookingDto)
                .toList();

        for (var item : listItemBookings) {
            var lastBooking = bookingRepository.findLastBookingByItemIdAndDateTime(item.getId(), LocalDateTime.now());
            var nextBooking = bookingRepository.findNextBookingByItemIdAndDateTime(item.getId(), LocalDateTime.now());
            var comments = commentRepository.findAllByItemIdOrderByCreatedDesc(item.getId());

            if (lastBooking != null)
                item.setLastBooking(BookingMapper.mapToBookingShortDto(lastBooking));
            if (nextBooking != null)
                item.setLastBooking(BookingMapper.mapToBookingShortDto(nextBooking));
            if (comments != null)
                item.setComments(comments.stream().map(CommentMapper::mapToCommentResponseDto).toList());
        }

        return listItemBookings;
    }

    @Override
    public Item createItem(Long userId, ItemDto itemDto) throws NotFoundException, ValidationException {
        validateUserById(userId);
        validateItemData(itemDto);

        return itemRepository.save(ItemMapper.mapToItem(itemDto, userId));
    }

    @Override
    public Item updateItem(Long userId, Long itemId, ItemDto itemDto) throws NotFoundException, ValidationException {
        validateUserById(userId);

        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Вещь с id = " + itemId + " не найдена."));
        if (!item.getOwner().getId().equals(userId))
            throw new ValidationException("Пользователь id = " + userId + " не владелец вещи id = " + itemId);

        if (itemDto.getName() != null)
            item.setName(itemDto.getName());
        if (itemDto.getDescription() != null)
            item.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null)
            item.setAvailable(itemDto.getAvailable());

        return itemRepository.save(item);
    }

    @Override
    public ItemBookingDto getItemBookingDtoById(Long itemId) throws NotFoundException {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Вещь с id = " + itemId + " не найдена."));
        ItemBookingDto itemBookingDto = ItemMapper.mapToItemBookingDto(item);

        var comments = commentRepository.findAllByItemIdOrderByCreatedDesc(item.getId());
        if (comments != null)
            itemBookingDto.setComments(comments.stream().map(CommentMapper::mapToCommentResponseDto).toList());

        return itemBookingDto;
    }

    public Item getItemById(Long itemId) throws NotFoundException {
        return itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Вещь с id = " + itemId + " не найдена."));
    }

    @Override
    public List<Item> searchForItems(String text) {
        if (!StringUtils.hasText(text))
            return List.of();
        else
            return itemRepository.searchForItems(text);
    }

    @Override
    public Comment postComment(Long itemId, Long userId, CommentCreateDto commentCreate) throws NotFoundException, ValidationException {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Вещь с id = " + itemId + " не найдена."));

        if (!bookingRepository.existsByBookerIdAndItemIdAndEndBefore(userId, itemId, LocalDateTime.now()))
            throw new ValidationException("Пользователь с id = " + userId + " не может оставить комментарий, т.к. он ранее не бронировал вещь с id = " + itemId);

        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден."));

        return commentRepository.save(CommentMapper.mapToComment(commentCreate, user, item));
    }


    private void validateUserById(Long userId) throws NotFoundException {
        if (userId == null || !userRepository.existsById(userId))
            throw new NotFoundException("Пользователь с id = " + userId + " не найден.");
    }

    private void validateItemData(ItemDto itemDto) throws ValidationException {
        if (!StringUtils.hasText(itemDto.getName())
                || !StringUtils.hasText(itemDto.getDescription())
                || itemDto.getAvailable() == null)
            throw new ValidationException("Некорректные входные данные.");
    }
}
