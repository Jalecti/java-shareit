package ru.practicum.shareit.item;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;


@Data
@EqualsAndHashCode(of = {"id"})
public class Item {
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotNull
    private Boolean available;

    @NotNull
    private User owner;

    private ItemRequest request;
}
