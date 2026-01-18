package ru.practicum.shareit.request;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

//класс отвечает за запрос требуемой вещи, которой не оказалось в шеринге
@Entity
@Table(name = "requests")
@EqualsAndHashCode(of = "id")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "description", nullable = false)
    private String description;//текст запроса, содержащий описание требуемой вещи

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;//пользователь создавший запрос

    @Column(name = "created", nullable = false)
    private LocalDateTime created = LocalDateTime.now();
}
