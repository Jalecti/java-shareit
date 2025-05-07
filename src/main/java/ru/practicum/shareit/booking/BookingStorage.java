package ru.practicum.shareit.booking;


import java.util.Collection;
import java.util.Optional;

public interface BookingStorage {

    Collection<Booking> findAll();

    Booking save(Booking booking);

    Booking update(Booking newBooking);

    void delete(Long bookingId);

    Optional<Booking> findById(Long bookingId);

}