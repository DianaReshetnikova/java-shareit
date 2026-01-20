package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/*
Класс-DTO, предназначен для возврата информации о запросе вещи и списков ответов на нее других пользователей
*/
@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class ItemRequestDto {
    private Long id;
    private Long requesterId;
    private String description;
    private LocalDateTime created;
    private List<ItemResponseDto> items;
}
