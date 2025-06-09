package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.BookingStatusException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @Mock
    private BookingStorage bookingRepository;
    @Mock
    private UserService userService;
    @Mock
    private ItemService itemService;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    void save_whenValidBooking_thenReturnsBookingDto() {
        Long bookerId = 1L;
        Long itemId = 10L;
        NewBookingRequest request = new NewBookingRequest();
        request.setItemId(itemId);
        request.setStart(LocalDateTime.now().plusHours(1));
        request.setEnd(LocalDateTime.now().plusDays(1));

        User booker = new User(bookerId, "booker", "booker@email.com");
        Item item = new Item(itemId, "item", "desc", true, new User(), null);
        Booking booking = new Booking(100L, request.getStart(), request.getEnd(), item, booker, BookingStatus.WAITING);

        Mockito.when(userService.checkUser(bookerId)).thenReturn(booker);
        Mockito.when(itemService.checkAvailableItem(itemId)).thenReturn(item);
        Mockito.when(bookingRepository.save(Mockito.any())).thenReturn(booking);

        BookingDto result = bookingService.save(bookerId, request);

        assertEquals(itemId, result.getItem().getId());
        assertEquals(bookerId, result.getBooker().getId());
        assertEquals(BookingStatus.WAITING, result.getStatus());

        Mockito.verify(bookingRepository).save(Mockito.any());
    }

    @Test
    void findAllByBookerIdAndState_whenStateAll_thenReturnsAllBookings() {
        Long bookerId = 1L;
        User booker = new User(bookerId, "booker", "email");
        Item item = new Item(2L, "item", "desc", true, new User(), null);
        Booking booking = new Booking(10L, LocalDateTime.now(), LocalDateTime.now().plusDays(1), item, booker, BookingStatus.APPROVED);

        Mockito.when(userService.checkUser(bookerId)).thenReturn(booker);
        Mockito.when(bookingRepository.findByBookerIdOrderByStart(bookerId)).thenReturn(List.of(booking));

        Collection<BookingDto> result = bookingService.findAllByBookerIdAndState(bookerId, BookingState.ALL);

        assertEquals(1, result.size());
        assertEquals(10L, result.iterator().next().getId());
        Mockito.verify(userService).checkUser(bookerId);
        Mockito.verify(bookingRepository).findByBookerIdOrderByStart(bookerId);
    }

    @Test
    void findAllByOwnerIdAndState_whenStateAll_thenReturnsAllBookings() {
        Long ownerId = 2L;
        User owner = new User(ownerId, "owner", "owner@email.com");
        User booker = new User(1L, "booker", "booker@email.com");
        Item item = new Item(5L, "item", "desc", true, owner, null);
        Booking booking = new Booking(20L, LocalDateTime.now(), LocalDateTime.now().plusDays(1), item, booker, BookingStatus.APPROVED);

        Mockito.when(userService.checkUser(ownerId)).thenReturn(owner);
        Mockito.when(bookingRepository.findByItemOwnerIdOrderByStart(ownerId)).thenReturn(List.of(booking));

        Collection<BookingDto> result = bookingService.findAllByOwnerIdAndState(ownerId, BookingState.ALL);

        assertEquals(1, result.size());
        assertEquals(20L, result.iterator().next().getId());
        Mockito.verify(userService).checkUser(ownerId);
        Mockito.verify(bookingRepository).findByItemOwnerIdOrderByStart(ownerId);
    }

    @Test
    void findBookingById_whenUserIsBooker_thenReturnsBookingDto() {
        Long userId = 1L;
        User booker = new User(userId, "booker", "booker@email.com");
        User owner = new User(2L, "owner", "owner@email.com");
        Item item = new Item(3L, "item", "desc", true, owner, null);
        Booking booking = new Booking(5L, LocalDateTime.now(), LocalDateTime.now().plusDays(1), item, booker, BookingStatus.APPROVED);

        Mockito.when(bookingRepository.findById(5L)).thenReturn(Optional.of(booking));

        BookingDto result = bookingService.findBookingById(userId, 5L);

        assertEquals(5L, result.getId());
        assertEquals(userId, result.getBooker().getId());
        Mockito.verify(bookingRepository).findById(5L);
    }

    @Test
    void findBookingById_whenUserIsNeitherBookerNorOwner_thenThrowsForbiddenException() {
        User booker = new User(1L, "booker", "booker@email.com");
        User owner = new User(2L, "owner", "owner@email.com");
        Item item = new Item(3L, "item", "desc", true, owner, null);
        Booking booking = new Booking(6L, LocalDateTime.now(), LocalDateTime.now().plusDays(1), item, booker, BookingStatus.APPROVED);

        Mockito.when(bookingRepository.findById(6L)).thenReturn(Optional.of(booking));

        assertThrows(ForbiddenException.class, () -> bookingService.findBookingById(999L, 6L));
    }

    @Test
    void approveBooking_whenApproved_thenStatusUpdatedToApproved() {
        Long bookingId = 1L;
        Long userId = 2L;
        User booker = new User(3L, "booker", "booker@email.com");
        User owner = new User(userId, "owner", "owner@email.com");
        Item item = new Item(5L, "item", "desc", true, owner, null);
        Booking booking = new Booking(bookingId, LocalDateTime.now(), LocalDateTime.now().plusDays(1), item, booker, BookingStatus.WAITING);

        Mockito.when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        Mockito.doNothing().when(itemService).checkOwner(userId, item);
        Mockito.when(bookingRepository.save(Mockito.any())).thenReturn(booking);

        BookingDto result = bookingService.approveBooking(userId, bookingId, true);

        assertEquals(BookingStatus.APPROVED, result.getStatus());
        Mockito.verify(bookingRepository).save(Mockito.any());
    }

    @Test
    void approveBooking_whenAlreadyApproved_thenThrowsBookingStatusException() {
        Long bookingId = 2L;
        User booker = new User(3L, "booker", "booker@email.com");
        User owner = new User(2L, "owner", "owner@email.com");
        Item item = new Item(6L, "item", "desc", true, owner, null);
        Booking booking = new Booking(bookingId, LocalDateTime.now(), LocalDateTime.now().plusDays(1), item, booker, BookingStatus.APPROVED);

        Mockito.when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(BookingStatusException.class, () -> bookingService.approveBooking(owner.getId(), bookingId, true));
    }

}