package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemShortDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BookingMapper {
    public static BookingDto mapToBookingDto(Booking booking, ItemShortDto itemShortDto, UserDto userDto) {
        BookingDto bookingDto = new BookingDto();

        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setItem(itemShortDto);
        bookingDto.setBooker(userDto);
        bookingDto.setStatus(booking.getStatus());

        return bookingDto;
    }

    public static BookingShortDto mapToShortDto(Booking booking) {
        BookingShortDto bookingShortDto = new BookingShortDto();

        bookingShortDto.setId(booking.getId());
        bookingShortDto.setStart(booking.getStart());
        bookingShortDto.setEnd(booking.getEnd());
        bookingShortDto.setStatus(booking.getStatus());
        bookingShortDto.setItemId(booking.getItem().getId());
        bookingShortDto.setBookerId(booking.getBooker().getId());


        return bookingShortDto;
    }

    public static Booking mapToBooking(NewBookingRequest request, Item item, User booker, BookingStatus status) {
        Booking booking = new Booking();

        booking.setStart(request.getStart());
        booking.setEnd(request.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(status);

        return booking;
    }

}
