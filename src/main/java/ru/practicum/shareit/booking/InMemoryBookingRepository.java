package ru.practicum.shareit.booking;

import org.springframework.stereotype.Repository;

import java.util.*;


@Repository
public class InMemoryBookingRepository implements BookingStorage {
    private final Map<Long, Booking> bookings;
    private Long counter;

    public InMemoryBookingRepository() {
        counter = 0L;
        bookings = new HashMap<>();
    }

    @Override
    public Collection<Booking> findAll() {
        return new ArrayList<>(bookings.values());
    }

    @Override
    public Booking save(Booking booking) {
        booking.setId(++counter);
        bookings.put(booking.getId(), booking);
        return booking;
    }

    @Override
    public Booking update(Booking newBooking) {
        Booking bookingToUpdate = bookings.get(newBooking.getId());
        bookingToUpdate.setEnd(newBooking.getEnd());
        bookingToUpdate.setStatus(newBooking.getStatus());
        return bookingToUpdate;
    }

    @Override
    public void delete(Long bookingId) {
        bookings.remove(bookingId);
    }

    @Override
    public Optional<Booking> findById(Long bookingId) {
        return Optional.ofNullable(bookings.get(bookingId));
    }
}
