package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingsDates;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public final class ItemMapper {

    private ItemMapper() {

    }

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null
        );
    }

    public static Item toItem(ItemDto itemDto) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                null,
                null
        );
    }

    public static List<ItemDto> mapListToDto(List<Item> items) {
        return items.stream().map(ItemMapper::toItemDto).toList();
    }

    public static ItemDtoWithBookingsDates toItemDtoWithBookingsDates(
            Item item,
            Booking lastBooking,
            Booking nextBooking
    ) {
        BookingItemDto lastBookingDto = null;
        if (lastBooking != null) {
            lastBookingDto = new BookingItemDto(
                    lastBooking.getId(),
                    lastBooking.getStart(),
                    lastBooking.getEnd(),
                    lastBooking.getStatus(),
                    lastBooking.getBooker().getId()
            );
        }

        BookingItemDto nextBookingDto = null;
        if (nextBooking != null) {
            nextBookingDto = new BookingItemDto(
                    nextBooking.getId(),
                    nextBooking.getStart(),
                    nextBooking.getEnd(),
                    nextBooking.getStatus(),
                    nextBooking.getBooker().getId()
            );
        }

        return new ItemDtoWithBookingsDates(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null,
                lastBookingDto,
                nextBookingDto
        );
    }
}