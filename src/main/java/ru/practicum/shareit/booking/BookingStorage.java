package ru.practicum.shareit.booking;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BookingStorage extends JpaRepository<Booking, Long> {

    //Booker
    Collection<Booking> findByBookerIdOrderByStart(Long bookerId);

    Collection<Booking> findByBookerIdAndEndAfterOrderByStart(Long bookerId, LocalDateTime now);

    Collection<Booking> findByBookerIdAndEndBeforeOrderByStart(Long bookerId, LocalDateTime now);

    Collection<Booking> findByBookerIdAndStartAfterOrderByStart(Long bookerId, LocalDateTime now);

    Collection<Booking> findByBookerIdAndStatusOrderByStart(Long bookerId, BookingStatus status);

    Boolean existsByBookerIdAndItemIdAndEndBefore(Long bookerId, Long itemId, LocalDateTime now);

    //Owner
    Collection<Booking> findByItemOwnerIdOrderByStart(Long ownerId);

    Collection<Booking> findByItemOwnerIdAndEndAfterOrderByStart(Long ownerId, LocalDateTime now);

    Collection<Booking> findByItemOwnerIdAndEndBeforeOrderByStart(Long ownerId, LocalDateTime now);

    Collection<Booking> findByItemOwnerIdAndStartAfterOrderByStart(Long ownerId, LocalDateTime now);

    Collection<Booking> findByItemOwnerIdAndStatusOrderByStart(Long ownerId, BookingStatus status);

    Optional<Booking> findFirstByItemOwnerIdAndEndBeforeOrderByEndDesc(Long ownerId, LocalDateTime now);

    Optional<Booking> findFirstByItemOwnerIdAndStartAfterOrderByStart(Long ownerId, LocalDateTime now);

    //Other

    @Query("SELECT b " +
            "FROM Booking b " +
            "WHERE b.item.id IN ?1 AND b.end < ?2 " +
            "ORDER BY b.end DESC")
    List<Booking> findPrevBookings(Collection<Long> itemIds, LocalDateTime now);

    @Query("SELECT b " +
            "FROM Booking b " +
            "WHERE b.item.id IN ?1 AND b.start > ?2 " +
            "ORDER BY b.start ASC")
    List<Booking> findNextBookings(Collection<Long> itemIds, LocalDateTime now);

}
