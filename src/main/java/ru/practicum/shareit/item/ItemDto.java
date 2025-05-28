package ru.practicum.shareit.item;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.shareit.booking.BookingShortDto;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.UserDto;

import java.util.Collection;


@Data
@EqualsAndHashCode(of = {"id"})
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private UserDto owner;
    private ItemRequest request;
    private BookingShortDto lastBooking;
    private BookingShortDto nextBooking;
    private Collection<CommentDto> comments;
}
