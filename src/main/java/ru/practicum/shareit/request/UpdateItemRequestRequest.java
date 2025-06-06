package ru.practicum.shareit.request;

import lombok.Data;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;


@Data
public class UpdateItemRequestRequest {
    private Long id;
    private String description;
    private User requestor;
    private LocalDateTime created;

    public boolean hasDescription() {
        return !(description == null || description.isBlank());
    }
}
