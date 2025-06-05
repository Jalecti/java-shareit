package ru.practicum.shareit.booking;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NewBookingRequest {
    private Long itemId;
    private LocalDateTime start;
    private LocalDateTime end;
}
