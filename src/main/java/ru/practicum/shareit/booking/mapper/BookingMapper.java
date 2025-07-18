package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.ItemDtoShort;
import ru.practicum.shareit.user.dto.UserDtoShort;

import java.util.List;

public final class BookingMapper {

    private BookingMapper() {

    }

    public static BookingDto mapToDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem().getId(),
                booking.getBooker().getId(),
                booking.getStatus().toString()
        );
    }

    public static Booking mapFromDto(BookingDto dto) {
        return new Booking(dto.getId(),
                dto.getStart(),
                dto.getEnd(),
                null,
                null,
                null
        );
    }

    public static BookingDtoResponse mapToDtoResponse(Booking booking) {
        return new BookingDtoResponse(booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                new ItemDtoShort(booking.getItem().getId(), booking.getItem().getName()),
                new UserDtoShort(booking.getBooker().getId(), booking.getBooker().getName()),
                booking.getStatus());
    }

    public static List<BookingDtoResponse> mapListToDtoResponses(List<Booking> bookings) {
        return bookings.stream().map(BookingMapper::mapToDtoResponse).toList();
    }
}