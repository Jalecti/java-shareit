package ru.practicum.shareit.booking;


import lombok.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BookingStorage extends JpaRepository<Booking, Long> {

    @EntityGraph(value = "Booking.forMapping")
    @NonNull
    @Override
    Optional<Booking> findById(@NonNull Long id);

    //Booker
    @EntityGraph(value = "Booking.forMapping")
    Collection<Booking> findByBookerIdOrderByStart(Long bookerId);

    @EntityGraph(value = "Booking.forMapping")
    Collection<Booking> findByBookerIdAndEndAfterOrderByStart(Long bookerId, LocalDateTime now);

    @EntityGraph(value = "Booking.forMapping")
    Collection<Booking> findByBookerIdAndEndBeforeOrderByStart(Long bookerId, LocalDateTime now);

    @EntityGraph(value = "Booking.forMapping")
    Collection<Booking> findByBookerIdAndStartAfterOrderByStart(Long bookerId, LocalDateTime now);

    @EntityGraph(value = "Booking.forMapping")
    Collection<Booking> findByBookerIdAndStatusOrderByStart(Long bookerId, BookingStatus status);

    Boolean existsByBookerIdAndItemIdAndEndBefore(Long bookerId, Long itemId, LocalDateTime now);

    //Owner
    @EntityGraph(value = "Booking.forMapping")
    Collection<Booking> findByItemOwnerIdOrderByStart(Long ownerId);

    @EntityGraph(value = "Booking.forMapping")
    Collection<Booking> findByItemOwnerIdAndEndAfterOrderByStart(Long ownerId, LocalDateTime now);

    @EntityGraph(value = "Booking.forMapping")
    Collection<Booking> findByItemOwnerIdAndEndBeforeOrderByStart(Long ownerId, LocalDateTime now);

    @EntityGraph(value = "Booking.forMapping")
    Collection<Booking> findByItemOwnerIdAndStartAfterOrderByStart(Long ownerId, LocalDateTime now);

    @EntityGraph(value = "Booking.forMapping")
    Collection<Booking> findByItemOwnerIdAndStatusOrderByStart(Long ownerId, BookingStatus status);

    @EntityGraph(value = "Booking.forMapping")
    Optional<Booking> findFirstByItemOwnerIdAndEndBeforeOrderByEndDesc(Long ownerId, LocalDateTime now);

    @EntityGraph(value = "Booking.forMapping")
    Optional<Booking> findFirstByItemOwnerIdAndStartAfterOrderByStart(Long ownerId, LocalDateTime now);

    //Other
    @EntityGraph(value = "Booking.forMapping")
    @Query("SELECT b " +
            "FROM Booking b " +
            "WHERE b.item.id IN ?1 AND b.end < ?2 " +
            "ORDER BY b.end DESC")
    List<Booking> findPrevBookings(Collection<Long> itemIds, LocalDateTime now);

    @EntityGraph(value = "Booking.forMapping")
    @Query("SELECT b " +
            "FROM Booking b " +
            "WHERE b.item.id IN ?1 AND b.start > ?2 " +
            "ORDER BY b.start ASC")
    List<Booking> findNextBookings(Collection<Long> itemIds, LocalDateTime now);

}
