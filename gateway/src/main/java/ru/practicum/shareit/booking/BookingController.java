package ru.practicum.shareit.booking;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.dto.BookingState;


@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
	private final BookingClient bookingClient;

	@PostMapping
	public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") Long userId,
			@RequestBody @Valid NewBookingRequest requestDto) {
		log.info("Creating booking {}, userId={}", requestDto, userId);
		return bookingClient.create(userId, requestDto);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> approve(@RequestHeader("X-Sharer-User-Id") Long userId,
							  @PathVariable Long bookingId, @RequestParam("approved") Boolean isApproved) {
		log.info("Approve booking {}, userId={}, approved={}", bookingId, userId, isApproved);
		return bookingClient.approveBooking(userId, bookingId, isApproved);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> findBookingById(@RequestHeader("X-Sharer-User-Id") Long userId,
			@PathVariable Long bookingId) {
		log.info("Get booking {}, userId={}", bookingId, userId);
		return bookingClient.findBookingById(userId, bookingId);
	}

	@GetMapping
	public ResponseEntity<Object> findAllByBookerIdAndState(@RequestHeader("X-Sharer-User-Id") Long userId,
											  @RequestParam(name = "state", defaultValue = "all") String stateParam) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
		log.info("Get booking with state {}, userId={}", stateParam, userId);
		return bookingClient.findAllByBookerIdAndState(userId, state);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> findAllByOwnerIdAndState(@RequestHeader("X-Sharer-User-Id") Long userId,
															@RequestParam(name = "state", defaultValue = "all") String stateParam) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
		log.info("Get booking by owner with state {}, userId={}", stateParam, userId);
		return bookingClient.findAllByOwnerIdAndState(userId, state);
	}
}
