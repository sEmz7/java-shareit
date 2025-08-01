package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;


@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
	private final BookingClient bookingClient;
	private static final String USER_ID_HEADER = "X-Sharer-User-Id";

	@PostMapping
	public ResponseEntity<Object> createBooking(@Valid @RequestBody BookingDto bookingDto,
												@Positive @RequestHeader(USER_ID_HEADER) long userId) {
		return bookingClient.createBooking(bookingDto, userId);
	}

	@PatchMapping("/{bookingId}")
	ResponseEntity<Object> update(@Positive @RequestHeader(USER_ID_HEADER) Long userId,
								  @Positive @PathVariable Long bookingId, @RequestParam boolean approved) {
		log.info("received PATCH request for user {}: booking={}, approved={}", userId, bookingId, approved);
		return bookingClient.update(userId, bookingId, approved);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBookingById(@Positive @PathVariable long bookingId) {
		return bookingClient.getBookingById(bookingId);
	}

	@GetMapping
	public ResponseEntity<Object> getUserBookings(@RequestParam(defaultValue = "ALL") String state,
												  @Positive @RequestHeader(USER_ID_HEADER) long userId) {
		return bookingClient.getUserBookings(state, userId);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getOwnerBookings(@RequestParam(defaultValue = "ALL") String state,
												   @Positive @RequestHeader(USER_ID_HEADER) long userId) {
		return bookingClient.getOwnerBookings(state, userId);
	}
}
