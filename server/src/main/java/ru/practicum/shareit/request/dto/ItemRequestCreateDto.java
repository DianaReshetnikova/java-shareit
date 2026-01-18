package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@Builder(toBuilder = true)
public class ItemRequestCreateDto {
    private String description;

    public ItemRequestCreateDto(@JsonProperty("description") String description) {
        this.description = description;
    }
}
