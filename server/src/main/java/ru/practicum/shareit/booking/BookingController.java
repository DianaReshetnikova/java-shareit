package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import java.util.List;


@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    //запрос от любого юзера на создание нового бронирования
    @PostMapping
    public BookingResponseDto requestForBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @Valid @RequestBody BookingCreateDto bookingDto) throws NotFoundException, ValidationException {
        return BookingMapper.mapToBookingResponseDto(bookingService.createBooking(userId, bookingDto));
    }

    //Подтверждение или отклонение запроса на бронирование. Может быть выполнено только владельцем вещи.
    //PATCH /bookings/{bookingId}?approved={approved}
    @PatchMapping("/{bookingId}")
    public BookingResponseDto updateBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @PathVariable Long bookingId,
                                            @RequestParam boolean approved) throws NotFoundException, ValidationException {
        return BookingMapper.mapToBookingResponseDto(bookingService.updateBooking(bookingId, approved, userId));
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBooking(@PathVariable Long bookingId,
                                         @RequestHeader("X-Sharer-User-Id") Long userId) throws NotFoundException, ValidationException {
        return BookingMapper.mapToBookingResponseDto(bookingService.getBooking(bookingId, userId));
    }

    //Получение списка всех бронирований текущего пользователя.
    //GET /bookings?state={state}
    @GetMapping()
    public List<BookingResponseDto> getBookingsOfUser(@RequestParam(defaultValue = "ALL") String state,
                                                      @RequestHeader("X-Sharer-User-Id") Long userId) throws NotFoundException, ValidationException {
        return bookingService.getBookingsOfUserByState(userId, state)
                .stream()
                .map(BookingMapper::mapToBookingResponseDto)
                .toList();
    }

    //Получение списка бронирований для всех вещей текущего пользователя.
    //GET /bookings/owner?state={state}
    @GetMapping("/owner")
    public List<BookingResponseDto> getAllBookingsOfOwner(@RequestParam(defaultValue = "ALL") String state,
                                                          @RequestHeader("X-Sharer-User-Id") Long userId) throws NotFoundException, ValidationException {
        return bookingService.getBookingsForAllItemsOfOwner(userId, state).stream()
                .map(BookingMapper::mapToBookingResponseDto)
                .toList();
    }
}
