package ru.practicum.shareit.item;

import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(of = {"id"})
public class ItemShortDto {
    private Long id;
    private String name;
    private Boolean available;
    private Long ownerId;
    private String ownerEmail;
}
