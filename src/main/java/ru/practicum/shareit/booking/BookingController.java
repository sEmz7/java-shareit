package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.Booking;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingDtoResponse> createBooking(@Valid @RequestBody BookingDto bookingDto,
                                                            @RequestHeader("X-Sharer-User-Id") long userId) {
        BookingDtoResponse createdBookingDto = bookingService.createBooking(bookingDto, userId);
        return ResponseEntity.ok().body(createdBookingDto);
    }
}
