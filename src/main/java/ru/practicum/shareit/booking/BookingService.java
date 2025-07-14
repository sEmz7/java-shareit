package ru.practicum.shareit.booking;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;

import java.util.List;

@Transactional(readOnly = true)
public interface BookingService {

    @Transactional
    BookingDtoResponse createBooking(BookingDto bookingDto, long userId);

    @Transactional
    BookingDtoResponse updateBookingStatus(long bookingId, boolean approved, long userId);

    BookingDtoResponse getBookingById(long bookingId);

    List<BookingDtoResponse> getUserBookings(String state, long userId);

    List<BookingDtoResponse> getOwnerBookings(String state, long ownerId);
}
