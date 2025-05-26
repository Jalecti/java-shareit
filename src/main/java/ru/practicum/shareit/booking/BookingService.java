package ru.practicum.shareit.booking;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;

public interface BookingService {

    BookingDto save(Long userId, NewBookingRequest newBookingRequest);

    Collection<BookingDto> findAllByBookerIdAndState(Long bookerId, BookingState state);

    Collection<BookingDto> findAllByOwnerIdAndState(Long ownerId, BookingState state);

    BookingDto findBookingById(Long userId, Long bookingId);

    BookingDto approveBooking(Long userId, Long bookingId, Boolean isApprove);

    Booking checkBooking(Long bookingId);

    BookingDto findPrev(Long ownerId);

    BookingDto findNext(Long ownerId);

    Boolean existsByBookerIdAndItemIdAndEndBefore(Long bookerId, Long itemId, LocalDateTime now);

    Map<Long, BookingDto> findAllPrevsByItemIds(Collection<Long> itemIds);

    Map<Long, BookingDto> findAllNextsByItemIds(Collection<Long> itemIds);

}
