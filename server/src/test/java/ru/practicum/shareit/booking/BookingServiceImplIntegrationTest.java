package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class BookingServiceImplIntegrationTest {
    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserStorage userRepository;

    @Autowired
    private ItemStorage itemRepository;

    @Autowired
    private BookingStorage bookingRepository;

    @BeforeEach
    void beforeEach() {
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void save_whenValidRequest_thenReturnsBookingDto() {
        User owner = userRepository.save(new User(null, "owner", "owner@example.com"));
        User booker = userRepository.save(new User(null, "booker", "booker@example.com"));
        Item item = itemRepository.save(new Item(null, "item", "desc", true, owner, null));

        LocalDateTime now = LocalDateTime.now();
        NewBookingRequest request = new NewBookingRequest(item.getId(), now.plusHours(1), now.plusHours(2));
        BookingDto saved = bookingService.save(booker.getId(), request);

        BookingDto found = bookingService.findBookingById(owner.getId(), saved.getId());

        assertEquals(item.getId(), found.getItem().getId());
        assertEquals(booker.getId(), found.getBooker().getId());
        assertEquals(BookingStatus.WAITING, found.getStatus());
    }

    @Test
    void approve_whenValidBooking_thenChangesStatusToApproved() {
        User owner = userRepository.save(new User(null, "owner", "owner@example.com"));
        User booker = userRepository.save(new User(null, "booker", "booker@example.com"));
        Item item = itemRepository.save(new Item(null, "item", "desc", true, owner, null));

        LocalDateTime now = LocalDateTime.now();
        NewBookingRequest request = new NewBookingRequest(item.getId(), now.plusHours(1), now.plusHours(2));
        BookingDto saved = bookingService.save(booker.getId(), request);

        BookingDto approved = bookingService.approveBooking(owner.getId(), saved.getId(), true);

        assertEquals(BookingStatus.APPROVED, approved.getStatus());
    }

    @Test
    void findAllByBookerIdAndState_whenExists_thenReturnsBookings() {
        User owner = userRepository.save(new User(null, "owner", "owner@example.com"));
        User booker = userRepository.save(new User(null, "booker", "booker@example.com"));
        Item item = itemRepository.save(new Item(null, "item", "desc", true, owner, null));

        LocalDateTime now = LocalDateTime.now();
        NewBookingRequest request = new NewBookingRequest(item.getId(), now.plusHours(1), now.plusHours(2));
        bookingService.save(booker.getId(), request);

        Collection<BookingDto> bookings = bookingService.findAllByBookerIdAndState(booker.getId(), BookingState.ALL);

        assertEquals(1, bookings.size());
    }

    @Test
    void findAllByOwnerIdAndState_whenExists_thenReturnsBookings() {
        User owner = userRepository.save(new User(null, "owner", "owner@example.com"));
        User booker = userRepository.save(new User(null, "booker", "booker@example.com"));
        Item item = itemRepository.save(new Item(null, "item", "desc", true, owner, null));

        LocalDateTime now = LocalDateTime.now();
        NewBookingRequest request = new NewBookingRequest(item.getId(), now.plusHours(1), now.plusHours(2));
        bookingService.save(booker.getId(), request);

        Collection<BookingDto> bookings = bookingService.findAllByOwnerIdAndState(owner.getId(), BookingState.ALL);

        assertEquals(1, bookings.size());
    }
}