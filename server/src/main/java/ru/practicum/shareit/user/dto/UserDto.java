package ru.practicum.shareit.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

//Структура данных, которые контроллеры должны возвращать пользователю или получать от него
@Data
@EqualsAndHashCode(of = "email")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    //скрыть поле id в определении модели API POST, но при этом отображать его в ответе API GET
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    private String name;
    private String email;

    public UserDto(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
