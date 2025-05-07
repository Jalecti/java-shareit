package ru.practicum.shareit.booking;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;


@Data
public class UpdateBookingRequest {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Item item;
    private User booker;
    private BookingStatus status;

    public boolean hasEnd() {
        return !(end == null);
    }

    public boolean hasStatus() {
        return !(status == null);
    }
}

