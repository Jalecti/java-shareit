package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.User;
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
        checkBookingStartEndDate(newBookingRequest);
        Item item = itemService.checkAvailableItem(newBookingRequest.getItemId());
        Booking booking = BookingMapper.mapToBooking(newBookingRequest, item, booker, BookingStatus.WAITING);
        bookingRepository.save(booking);
        log.info("Пользователь {} создал запрос на бронирование предмета {} с ID: {}",
                booker.getEmail(),
                item.getName(),
                item.getId());
        return BookingMapper.mapToBookingDto(booking, itemService.findItemById(booking.getItem().getId()));
    }

    @Override
    public Collection<BookingDto> findAllByBookerIdAndState(Long bookerId, BookingState state) {
        userService.checkUser(bookerId);
        return getFindMethodByStateForBooker(bookerId, state)
                .stream()
                .map(booking -> BookingMapper.mapToBookingDto(booking,
                        itemService.findItemById(booking.getItem().getId())))
                .toList();
    }

    @Override
    public Collection<BookingDto> findAllByOwnerIdAndState(Long ownerId, BookingState state) {
        userService.checkUser(ownerId);
        return getFindMethodByStateForOwner(ownerId, state)
                .stream()
                .map(booking -> BookingMapper.mapToBookingDto(booking,
                        itemService.findItemById(booking.getItem().getId())))
                .toList();
    }

    @Override
    public BookingDto findBookingById(Long userId, Long bookingId) {
        Booking booking = checkBooking(bookingId);
        checkAuthorAndItemOwner(userId, booking);
        return BookingMapper.mapToBookingDto(booking, itemService.findItemById(booking.getItem().getId()));
    }

    @Transactional
    @Override
    public BookingDto approveBooking(Long userId, Long bookingId, Boolean isApproved) {
        Booking booking = checkBooking(bookingId);
        Item item = booking.getItem();
        itemService.checkOwner(userId, item);
        BookingStatus newStatus = isApproved ? BookingStatus.APPROVED : BookingStatus.REJECTED;
        booking.setStatus(newStatus);
        bookingRepository.save(booking);
        log.info("Владелец {} обновил статус запроса на бронирование предмета {} с ID: {} на статус: {}",
                userId, item.getName(), item.getId(), booking.getStatus().name());
        return BookingMapper.mapToBookingDto(booking, itemService.findItemById(booking.getItem().getId()));
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

    private void checkBookingStartEndDate(NewBookingRequest booking) {
        LocalDateTime start = booking.getStart();
        LocalDateTime end = booking.getEnd();
        if (start.equals(end) || start.isAfter(end)) {
            log.error("Ошибка с временем начала и конца бронирования start: {} end: {}", start, end);
            throw new ValidationException("Ошибка с временем начала и конца бронирования " +
                    "start: " + start + " end: " + end);
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

    private Collection<Booking> getFindMethodByStateForOwner(Long bookerId, BookingState state) {
        return switch (state) {
            case ALL -> bookingRepository.findByItemOwnerIdOrderByStart(bookerId);
            case CURRENT -> bookingRepository.findByItemOwnerIdAndEndAfterOrderByStart(bookerId, LocalDateTime.now());
            case PAST -> bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStart(bookerId, LocalDateTime.now());
            case FUTURE -> bookingRepository.findByItemOwnerIdAndStartAfterOrderByStart(bookerId, LocalDateTime.now());
            case WAITING -> bookingRepository.findByItemOwnerIdAndStatusOrderByStart(bookerId, BookingStatus.WAITING);
            case REJECTED -> bookingRepository.findByItemOwnerIdAndStatusOrderByStart(bookerId, BookingStatus.REJECTED);
        };
    }

    public BookingDto findPrev(Long ownerId) {
        Optional<Booking> prev = bookingRepository.findFirstByItemOwnerIdAndEndBeforeOrderByEndDesc(ownerId, LocalDateTime.now());
        if (prev.isEmpty()) return null;
        return BookingMapper.mapToBookingDto(prev.get(), itemService.findItemById(prev.get().getItem().getId()));
    }

    public BookingDto findNext(Long ownerId) {
        Optional<Booking> next = bookingRepository.findFirstByItemOwnerIdAndStartAfterOrderByStart(ownerId, LocalDateTime.now());
        if (next.isEmpty()) return null;
        return BookingMapper.mapToBookingDto(next.get(), itemService.findItemById(next.get().getItem().getId()));
    }

    @Override
    public Boolean existsByBookerIdAndItemIdAndEndBefore(Long bookerId, Long itemId, LocalDateTime now) {
        return bookingRepository.existsByBookerIdAndItemIdAndEndBefore(bookerId, itemId, now);
    }

    @Override
    public Map<Long, BookingDto> findAllPrevsByItemIds(Collection<Long> itemIds) {
        Map<Long, ItemDto> itemDtoMap = itemService.findItemDtoMapByIds(itemIds);
        return bookingRepository.findPrevBookings(itemIds, LocalDateTime.now()).stream()
                .collect(Collectors.toMap(
                        b -> b.getItem().getId(),
                        b -> BookingMapper.mapToBookingDto(b, itemService.findItemById(b.getItem().getId())),
                        (b1, b2) -> b1
                ));
    }

    @Override
    public Map<Long, BookingDto> findAllNextsByItemIds(Collection<Long> itemIds) {
        Map<Long, ItemDto> itemDtoMap = itemService.findItemDtoMapByIds(itemIds);
        return bookingRepository.findNextBookings(itemIds, LocalDateTime.now()).stream()
                .collect(Collectors.toMap(
                        b -> b.getItem().getId(),
                        b -> BookingMapper.mapToBookingDto(b, itemService.findItemById(b.getItem().getId())),
                        (b1, b2) -> b1
                ));
    }

}
