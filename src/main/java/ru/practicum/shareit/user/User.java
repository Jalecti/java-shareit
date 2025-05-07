package ru.practicum.shareit.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(of = {"id"})
public class User {

    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String email;
}
