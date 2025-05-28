package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;


@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                             @Valid @RequestBody NewBookingRequest newBookingRequest) {
        return bookingService.save(userId, newBookingRequest);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approve(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable Long bookingId, @RequestParam("approved") Boolean isApproved) {
        return bookingService.approveBooking(userId, bookingId, isApproved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto findBookingById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @PathVariable Long bookingId) {
        return bookingService.findBookingById(userId, bookingId);
    }

    @GetMapping
    public Collection<BookingDto> findAllByBookerIdAndState(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                                            @RequestParam(required = false, defaultValue = "all") String state) {
        return bookingService.findAllByBookerIdAndState(bookerId, BookingState.from(state));
    }

    @GetMapping("/owner")
    public Collection<BookingDto> findAllByOwnerIdAndState(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                           @RequestParam(required = false, defaultValue = "all") String state) {
        return bookingService.findAllByOwnerIdAndState(ownerId, BookingState.from(state));
    }
}
