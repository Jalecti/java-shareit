package ru.practicum.shareit.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.shareit.item.ItemShortDto;
import ru.practicum.shareit.user.UserDto;

import java.time.LocalDateTime;
import java.util.Collection;


@Data
@EqualsAndHashCode(of = {"id"})
public class ItemRequestDto {
    private Long id;
    private String description;
    private UserDto requestor;
    private Collection<ItemShortDto> items;
    private LocalDateTime created;
}
