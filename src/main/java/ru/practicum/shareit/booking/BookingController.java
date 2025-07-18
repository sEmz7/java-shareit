package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<BookingDtoResponse> createBooking(@Valid @RequestBody BookingDto bookingDto,
                                                            @RequestHeader(USER_ID_HEADER) long userId) {
        BookingDtoResponse createdBookingDto = bookingService.createBooking(bookingDto, userId);
        return ResponseEntity.ok().body(createdBookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDtoResponse> updateBookingStatus(@PathVariable long bookingId,
                                                                  @RequestParam boolean approved,
                                                                  @RequestHeader(USER_ID_HEADER) long userId) {
        BookingDtoResponse dto = bookingService.updateBookingStatus(bookingId, approved, userId);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDtoResponse> getBookingById(@PathVariable long bookingId) {
        return ResponseEntity.ok().body(bookingService.getBookingById(bookingId));
    }

    @GetMapping
    public ResponseEntity<List<BookingDtoResponse>> getUserBookings(@RequestParam(defaultValue = "ALL") String state,
                                                                    @RequestHeader(USER_ID_HEADER) long userId) {
    List<BookingDtoResponse> dtos = bookingService.getUserBookings(state, userId);
    return ResponseEntity.ok().body(dtos);
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingDtoResponse>> getOwnerBookings(@RequestParam(defaultValue = "ALL") String state,
                                                                     @RequestHeader(USER_ID_HEADER) long userId) {
        List<BookingDtoResponse> dtos = bookingService.getOwnerBookings(state, userId);
        return ResponseEntity.ok().body(dtos);
    }
}