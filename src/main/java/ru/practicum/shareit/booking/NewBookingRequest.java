package ru.practicum.shareit.booking;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Data
public class NewBookingRequest {
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
