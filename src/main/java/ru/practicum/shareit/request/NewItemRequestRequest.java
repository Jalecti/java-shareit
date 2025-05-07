package ru.practicum.shareit.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Data
public class NewItemRequestRequest {
    @NotBlank
    private String description;

    @NotNull
    private User requestor;

    @NotNull
    private LocalDateTime created;
}