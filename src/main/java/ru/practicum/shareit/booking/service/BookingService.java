package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import java.util.List;


public interface BookingService {
    Booking createBooking(Long bookerId, BookingCreateDto bookingCreateDto) throws NotFoundException, ValidationException;

    Booking updateBooking(Long bookingId, boolean approved, Long userId) throws NotFoundException, ValidationException;

    Booking getBooking(Long bookingId, Long userId) throws NotFoundException, ValidationException;

    List<Booking> getBookingsOfUserByState(Long userId, String state) throws NotFoundException, ValidationException;

    List<Booking> getBookingsForAllItemsOfOwner(Long userId, String state) throws NotFoundException, ValidationException;
}
