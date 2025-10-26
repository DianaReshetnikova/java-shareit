package ru.practicum.shareit.request;

import lombok.Data;

import java.time.LocalDateTime;

//класс отвечает за запрос требуемой вещи, которой не оказалось в шеринге
@Data
public class ItemRequest {
    private Long id;
    private String description;
    private Long requestorId;//создавший запрос
    private LocalDateTime created;
}
