package ru.practicum.shareit.booking;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;


@Data
@EqualsAndHashCode(of = {"id"})
public class BookingDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotNull
    private LocalDateTime start;

    @NotNull
    private LocalDateTime end;

    @NotNull
    private Item item;

    @NotNull
    private User booker;

    @NotNull
    private BookingStatus status;
}
