package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;

/*
Класс-DTO, предназначен для возврата ответов пользователей (вещей, предлагаемых к аренде)
на запрос о той или иной вещи
*/
@Data
@Builder(toBuilder = true)
public class ItemResponseDto {
    private Long id;//Id предлагаемой к аренде вещи
    private String name;//название
    private Long ownerId;//Id владельца предлагаемой вещи
}
