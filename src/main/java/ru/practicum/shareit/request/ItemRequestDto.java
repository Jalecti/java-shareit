package ru.practicum.shareit.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

import ru.practicum.shareit.user.User;


@Data
@EqualsAndHashCode(of = {"id"})
public class ItemRequestDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank
    private String description;

    @NotNull
    private User requestor;

    @NotNull
    private LocalDateTime created;
}
