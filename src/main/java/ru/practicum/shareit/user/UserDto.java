package ru.practicum.shareit.user;

import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(of = {"id"})
public class UserDto {
    private Long id;
    private String name;
    private String email;
}