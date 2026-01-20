package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;


@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/bookings", produces = "application/json")
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> requestForBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @Valid @RequestBody BookingCreateDto bookingDto) {
        return bookingClient.requestForBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @PathVariable Long bookingId,
                                                @RequestParam boolean approved) {
        return bookingClient.updateBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@PathVariable Long bookingId,
                                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingClient.getBooking(bookingId, userId);
    }

    @GetMapping()
    public ResponseEntity<Object> getBookingsOfUser(@RequestParam(defaultValue = "ALL") String state,
                                                    @RequestHeader("X-Sharer-User-Id") Long userId) {
        BookingState bookingState = BookingState.from(state)
                .orElseThrow(() -> new IllegalArgumentException("Неизвестное состояние бронирования: " + state));

        return bookingClient.getBookingsOfUser(bookingState, userId);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllBookingsOfOwner(@RequestParam(defaultValue = "ALL") String state,
                                                        @RequestHeader("X-Sharer-User-Id") Long userId) {
        BookingState bookingState = BookingState.from(state)
                .orElseThrow(() -> new IllegalArgumentException("Неизвестное состояние бронирования: " + state));

        return bookingClient.getAllBookingsOfOwner(bookingState, userId);
    }
}
