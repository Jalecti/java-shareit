package ru.practicum.shareit.booking;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.shareit.item.ItemShortDto;
import ru.practicum.shareit.user.UserDto;

import java.time.LocalDateTime;


@Data
@EqualsAndHashCode(of = {"id"})
public class BookingDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private ItemShortDto item;
    private UserDto booker;
    private BookingStatus status;
}
