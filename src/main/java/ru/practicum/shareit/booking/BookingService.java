package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;

import java.util.List;

public interface BookingService {

    BookingDtoResponse createBooking(BookingDto bookingDto, long userId);

    BookingDtoResponse updateBookingStatus(long bookingId, boolean approved, long userId);

    BookingDtoResponse getBookingById(long bookingId);

    List<BookingDtoResponse> getUserBookings(String state, long userId);

    List<BookingDtoResponse> getOwnerBookings(String state, long ownerId);
}
