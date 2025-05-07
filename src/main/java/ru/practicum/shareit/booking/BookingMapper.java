package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BookingMapper {
    public static BookingDto mapToBookingDto(Booking booking) {
        BookingDto bookingDto = new BookingDto();

        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setItem(booking.getItem());
        bookingDto.setBooker(booking.getBooker());
        bookingDto.setStatus(booking.getStatus());

        return bookingDto;
    }

    public static Booking mapToBooking(NewBookingRequest request) {
        Booking booking = new Booking();

        booking.setStart(request.getStart());
        booking.setEnd(request.getEnd());
        booking.setItem(request.getItem());
        booking.setBooker(request.getBooker());
        booking.setStatus(request.getStatus());

        return booking;
    }

    public static Booking updateBookingFields(Booking booking, UpdateBookingRequest request) {
        if (request.hasEnd()) {
            booking.setEnd(request.getEnd());
        }
        if (request.hasStatus()) {
            booking.setStatus(request.getStatus());
        }
        return booking;
    }
}
