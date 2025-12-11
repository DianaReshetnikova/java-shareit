package ru.practicum.shareit.booking.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    public Booking createBooking(Long bookerId, BookingCreateDto bookingCreateDto) throws NotFoundException, ValidationException {
        User booker = userService.getUserById(bookerId);
        Item item = itemService.getItemById(bookingCreateDto.getItemId());

        if (item.getAvailable() == false)
            throw new ValidationException("Вещь с id = " + item.getId() + " уже забронирована.");
        if (bookingCreateDto.getEnd() == null || bookingCreateDto.getStart() == null)
            throw new ValidationException("Start и end бронирования вещи не могут быть пустыми.");
        if (bookerId.equals(item.getOwner().getId()))
            throw new ValidationException("Владелец вещи не может забронировать свою же вещь.");
        if (bookingCreateDto.getEnd().isBefore(bookingCreateDto.getStart()) || bookingCreateDto.getStart().equals(bookingCreateDto.getEnd()))
            throw new ValidationException("Некорректно заданные начало и конец бронирования вещи.");
        if (bookingCreateDto.getStart().isBefore(LocalDateTime.now()))
            throw new ValidationException("Начало бронирования не может быть в прошлом.");
        if (bookingCreateDto.getEnd().isBefore(LocalDateTime.now()))
            throw new ValidationException("Конец бронирования не может быть в прошлом.");

        Booking booking = BookingMapper.mapToBooking(bookingCreateDto, booker, item, BookingStatus.WAITING);
        return bookingRepository.save(booking);
    }

    @Override
    public Booking updateBooking(Long bookingId, boolean approved, Long userId) throws NotFoundException, ValidationException {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Бронирование с id = " + bookingId + " не найдено."));
        Item item = booking.getItem();

        if (!userId.equals(item.getOwner().getId()))
            throw new ValidationException("Пользователь с id = " + userId + " не является владельцем забронированной вещи.");
        if (booking.getStatus() != BookingStatus.WAITING)
            throw new ValidationException("Статус бронирования должен быть WAITING.");

        User booker = userService.getUserById(userId);

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
            item.setAvailable(false);
        } else
            booking.setStatus(BookingStatus.REJECTED);

        itemService.updateItem(userId, item.getId(), ItemMapper.mapToItemDto(item));
        return bookingRepository.save(booking);
    }

    @Override
    public Booking getBooking(Long bookingId, Long userId) throws NotFoundException, ValidationException {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Бронирование с id = " + bookingId + " не найдено."));
        Item item = booking.getItem();
        User booker = userService.getUserById(userId);

        if (userId.equals(item.getOwner().getId()) || userId.equals(booking.getBooker().getId())) {
            return booking;
        } else
            throw new ValidationException("Пользователь с id = " + userId + " не является владельцем забронированной вещи / автором бронирования.");
    }

    @Override
    public List<Booking> getBookingsOfUserByState(Long userId, String state) throws NotFoundException, ValidationException {
        User booker = userService.getUserById(userId);
        BookingState bookingState = validateBookingState(state);

        List<Booking> bookings = null;
        switch (bookingState) {
            case ALL -> {
                bookings = bookingRepository.findByBookerIdOrderByStartDesc(userId);
                break;
            }
            case CURRENT -> {
                bookings = bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId,
                        LocalDateTime.now(), LocalDateTime.now());
                break;
            }
            case PAST -> {
                bookings = bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());
                break;
            }
            case FUTURE -> {
                bookings = bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now());
                break;
            }
            case WAITING -> {
                bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
                break;
            }
            case REJECTED -> {
                bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
                break;
            }
        }

        return bookings == null ? new ArrayList<>() : bookings;
    }

    @Override
    public List<Booking> getBookingsForAllItemsOfOwner(Long userId, String state) throws NotFoundException, ValidationException {
        User booker = userService.getUserById(userId);
        BookingState bookingState = validateBookingState(state);

        List<Booking> bookings = null;

        switch (bookingState) {
            case ALL -> {
                bookings = bookingRepository.findByOwnerIdAllBookingOfItems(userId);
                break;
            }
            case CURRENT -> {
                bookings = bookingRepository.findByOwnerIdCurrentBookingsOfItems(userId,
                        LocalDateTime.now(), LocalDateTime.now());
                break;
            }
            case PAST -> {
                bookings = bookingRepository.findByOwnerIdPastBookingsOfItems(userId, LocalDateTime.now());
                break;
            }
            case FUTURE -> {
                bookings = bookingRepository.findByOwnerIdFutureBookingsOfItems(userId, LocalDateTime.now());
                break;
            }
            case WAITING -> {
                bookings = bookingRepository.findByOwnerIdAndStatusBookingsOfItems(userId, BookingStatus.WAITING);
                break;
            }
            case REJECTED -> {
                bookings = bookingRepository.findByOwnerIdAndStatusBookingsOfItems(userId, BookingStatus.REJECTED);
                break;
            }
        }

        return bookings == null ? new ArrayList<>() : bookings;
    }

    private BookingState validateBookingState(String state) throws ValidationException {
        try {
            return BookingState.valueOf(state);
        } catch (Exception e) {
            throw new ValidationException("Не существует такого состояния бронирования = " + state);
        }
    }
}
