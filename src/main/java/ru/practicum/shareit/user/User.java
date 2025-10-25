package ru.practicum.shareit.user;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "email")
@Builder
public class User {
    private Long id;
    private String name;
    private String email;//по ТЗ два пользователя не могут иметь одинаковый адрес электронной почты
}
