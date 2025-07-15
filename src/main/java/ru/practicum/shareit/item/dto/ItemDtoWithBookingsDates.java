package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingItemDto;

@Data
@AllArgsConstructor
public class ItemDtoWithBookingsDates {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
    private BookingItemDto lastBooking;
    private BookingItemDto nextBooking;
}
