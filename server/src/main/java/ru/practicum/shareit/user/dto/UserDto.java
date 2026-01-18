package ru.practicum.shareit.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

//Структура данных, которые контроллеры должны возвращать пользователю или получать от него
@Data
@EqualsAndHashCode(of = "email")
@Builder
public class UserDto {
    //скрыть поле id в определении модели API POST, но при этом отображать его в ответе API GET
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    private String name;
    private String email;
}
