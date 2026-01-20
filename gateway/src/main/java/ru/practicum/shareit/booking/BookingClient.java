package ru.practicum.shareit.booking;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.DefaultUriBuilderFactory;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.client.BaseClient;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> requestForBooking(Long userId, BookingCreateDto bookingDto) {
        return post("", userId, bookingDto);
    }

    //PATCH /bookings/{bookingId}?approved={approved}
    public ResponseEntity<Object> updateBooking(Long userId, Long bookingId, boolean approved) {
        return patch("/" + bookingId + "?approved=" + approved, userId);
    }

    public ResponseEntity<Object> getBooking(Long bookingId, Long userId) {
        return get("/" + bookingId, userId);
    }

    //GET /bookings?state={state}
    public ResponseEntity<Object> getBookingsOfUser(BookingState state, Long userId) {
        Map<String, Object> params = Map.of("state", state.name());
        return get("", userId, params);
    }

    //GET /bookings/owner?state={state}
    public ResponseEntity<Object> getAllBookingsOfOwner(BookingState state, Long userId) {
        Map<String, Object> params = Map.of("state", state.name());
        return get("/owner", userId, params);
    }
}
