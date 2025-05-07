package ru.practicum.shareit.item;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.UserDto;


@Data
@EqualsAndHashCode(of = {"id"})
public class ItemDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotNull
    private Boolean available;

    @NotNull
    private UserDto owner;

    private ItemRequest request;
}
