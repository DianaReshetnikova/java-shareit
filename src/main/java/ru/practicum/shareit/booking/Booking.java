package ru.practicum.shareit.booking;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(of = "id")
public class Booking {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Long itemId; //вещь, которую пользователь бронирует
    private Long bookerId; //пользователь, который осуществляет бронирование
    private Status status;
}
