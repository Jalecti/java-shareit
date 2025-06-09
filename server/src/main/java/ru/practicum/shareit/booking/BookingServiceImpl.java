package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.BookingStatusException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingStorage bookingRepository;
    private final UserService userService;
    private final ItemService itemService;


    @Transactional
    @Override
    public BookingDto save(Long bookerId, NewBookingRequest newBookingRequest) {
        User booker = userService.checkUser(bookerId);
        Item item = itemService.checkAvailableItem(newBookingRequest.getItemId());
        Booking booking = BookingMapper.mapToBooking(newBookingRequest, item, booker, BookingStatus.WAITING);
        bookingRepository.save(booking);
        log.info("Пользователь {} создал запрос на бронирование предмета {} с ID: {}",
                booker.getEmail(),
                item.getName(),
                item.getId());
        return BookingMapper.mapToBookingDto(booking,
                ItemMapper.mapToShortDto(item),
                UserMapper.mapToUserDto(booker));
    }

    @Override
    public Collection<BookingDto> findAllByBookerIdAndState(Long bookerId, BookingState state) {
        userService.checkUser(bookerId);
        return getFindMethodByStateForBooker(bookerId, state)
                .stream()
                .map(booking -> BookingMapper.mapToBookingDto(
                        booking,
                        ItemMapper.mapToShortDto(booking.getItem()),
                        UserMapper.mapToUserDto(booking.getBooker())
                ))
                .toList();
    }

    @Override
    public Collection<BookingDto> findAllByOwnerIdAndState(Long ownerId, BookingState state) {
        userService.checkUser(ownerId);
        return getFindMethodByStateForOwner(ownerId, state)
                .stream()
                .map(booking -> BookingMapper.mapToBookingDto(
                        booking,
                        ItemMapper.mapToShortDto(booking.getItem()),
                        UserMapper.mapToUserDto(booking.getBooker())
                ))
                .toList();
    }

    @Override
    public BookingDto findBookingById(Long userId, Long bookingId) {
        Booking booking = checkBooking(bookingId);
        checkAuthorAndItemOwner(userId, booking);
        return BookingMapper.mapToBookingDto(
                booking,
                ItemMapper.mapToShortDto(booking.getItem()),
                UserMapper.mapToUserDto(booking.getBooker()
                ));
    }

    @Transactional
    @Override
    public BookingDto approveBooking(Long userId, Long bookingId, Boolean isApproved) {
        Booking booking = checkBooking(bookingId);
        checkBookingWaitingStatus(booking);
        Item item = booking.getItem();
        itemService.checkOwner(userId, item);
        BookingStatus newStatus = isApproved ? BookingStatus.APPROVED : BookingStatus.REJECTED;
        booking.setStatus(newStatus);
        bookingRepository.save(booking);
        log.info("Владелец {} обновил статус запроса на бронирование предмета {} с ID: {} на статус: {}",
                userId, item.getName(), item.getId(), booking.getStatus().name());
        return BookingMapper.mapToBookingDto(
                booking,
                ItemMapper.mapToShortDto(booking.getItem()),
                UserMapper.mapToUserDto(booking.getBooker()
                ));
    }

    @Override
    public Booking checkBooking(Long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(() -> {
            log.error("Бронирование не найдено с ID: {}", bookingId);
            return new NotFoundException("Бронирование не найдено с ID: " + bookingId);
        });
    }


    private void checkAuthorAndItemOwner(Long userId, Booking booking) {
        if (!(booking.getItem().getOwner().getId().equals(userId) || booking.getBooker().getId().equals(userId))) {
            log.error("Пользователь с ID: {} не является букером бронирования с ID: {}, " +
                    "а также не является владельцем предмета", userId, booking.getId());
            throw new ForbiddenException("Пользователь с ID: " + userId +
                    " не является букером бронирования с ID: " + booking.getId() +
                    " а также не является владельцем предмета");
        }
    }



    private Collection<Booking> getFindMethodByStateForBooker(Long bookerId, BookingState state) {
        return switch (state) {
            case ALL -> bookingRepository.findByBookerIdOrderByStart(bookerId);
            case CURRENT -> bookingRepository.findByBookerIdAndEndAfterOrderByStart(bookerId, LocalDateTime.now());
            case PAST -> bookingRepository.findByBookerIdAndEndBeforeOrderByStart(bookerId, LocalDateTime.now());
            case FUTURE -> bookingRepository.findByBookerIdAndStartAfterOrderByStart(bookerId, LocalDateTime.now());
            case WAITING -> bookingRepository.findByBookerIdAndStatusOrderByStart(bookerId, BookingStatus.WAITING);
            case REJECTED -> bookingRepository.findByBookerIdAndStatusOrderByStart(bookerId, BookingStatus.REJECTED);
        };
    }

    private Collection<Booking> getFindMethodByStateForOwner(Long ownerId, BookingState state) {
        return switch (state) {
            case ALL -> bookingRepository.findByItemOwnerIdOrderByStart(ownerId);
            case CURRENT -> bookingRepository.findByItemOwnerIdAndEndAfterOrderByStart(ownerId, LocalDateTime.now());
            case PAST -> bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStart(ownerId, LocalDateTime.now());
            case FUTURE -> bookingRepository.findByItemOwnerIdAndStartAfterOrderByStart(ownerId, LocalDateTime.now());
            case WAITING -> bookingRepository.findByItemOwnerIdAndStatusOrderByStart(ownerId, BookingStatus.WAITING);
            case REJECTED -> bookingRepository.findByItemOwnerIdAndStatusOrderByStart(ownerId, BookingStatus.REJECTED);
        };
    }

    @Override
    public BookingDto findPrev(Long ownerId) {
        Optional<Booking> prev = bookingRepository.findFirstByItemOwnerIdAndEndBeforeOrderByEndDesc(ownerId, LocalDateTime.now());
        Booking booking;
        if (prev.isEmpty()) {
            return null;
        } else {
            booking = prev.get();
        }
        return BookingMapper.mapToBookingDto(
                booking,
                ItemMapper.mapToShortDto(booking.getItem()),
                UserMapper.mapToUserDto(booking.getBooker()
                ));
    }

    @Override
    public BookingDto findNext(Long ownerId) {
        Optional<Booking> next = bookingRepository.findFirstByItemOwnerIdAndStartAfterOrderByStart(ownerId, LocalDateTime.now());
        Booking booking;
        if (next.isEmpty()) {
            return null;
        } else {
            booking = next.get();
        }
        return BookingMapper.mapToBookingDto(
                booking,
                ItemMapper.mapToShortDto(booking.getItem()),
                UserMapper.mapToUserDto(booking.getBooker()
                ));
    }

    @Override
    public Boolean existsByBookerIdAndItemIdAndEndBefore(Long bookerId, Long itemId, LocalDateTime now) {
        return bookingRepository.existsByBookerIdAndItemIdAndEndBefore(bookerId, itemId, now);
    }

    @Override
    public Map<Long, BookingShortDto> findAllPrevsByItemIds(Collection<Long> itemIds) {
        return bookingRepository.findPrevBookings(itemIds, LocalDateTime.now()).stream()
                .collect(Collectors.toMap(
                        b -> b.getItem().getId(),
                        BookingMapper::mapToShortDto,
                        (b1, b2) -> b1
                ));
    }

    @Override
    public Map<Long, BookingShortDto> findAllNextsByItemIds(Collection<Long> itemIds) {
        return bookingRepository.findNextBookings(itemIds, LocalDateTime.now()).stream()
                .collect(Collectors.toMap(
                        b -> b.getItem().getId(),
                        BookingMapper::mapToShortDto,
                        (b1, b2) -> b1
                ));
    }

    private void checkBookingWaitingStatus(Booking booking) {
        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            log.error("Бронирование с id={} не находится в статусе ожидания, текущий статус: {}",
                    booking.getId(), booking.getStatus().name());
            throw new BookingStatusException("Бронирование с id=" + booking.getId() +
                    " не находится в статусе ожидания, текущий статус: " + booking.getStatus().name());
        }
    }

}
