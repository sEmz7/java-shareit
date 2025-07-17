package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithDatesAndComments;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

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

    public static ItemDtoWithDatesAndComments toItemDtoWithBookingsDatesAndComments(
            Item item,
            Booking lastBooking,
            Booking nextBooking,
            List<CommentDto> commentDtos
    ) {
        BookingItemDto lastBookingDto = lastBooking != null
                ? new BookingItemDto(
                lastBooking.getId(),
                lastBooking.getStart(),
                lastBooking.getEnd(),
                lastBooking.getStatus(),
                lastBooking.getBooker().getId())
                : null;

        BookingItemDto nextBookingDto = nextBooking != null
                ? new BookingItemDto(
                nextBooking.getId(),
                nextBooking.getStart(),
                nextBooking.getEnd(),
                nextBooking.getStatus(),
                nextBooking.getBooker().getId())
                : null;

        return new ItemDtoWithDatesAndComments(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null,
                lastBookingDto,
                nextBookingDto,
                commentDtos
        );
    }

    public static Comment mapCommentFromDto(CommentDto commentDto) {
        return new Comment(
                commentDto.getId(),
                commentDto.getText(),
                null,
                null,
                null
        );
    }

    public static CommentDto mapCommentToDto(Comment comment) {
        return new CommentDto(comment.getId(), comment.getText(), comment.getAuthor().getName(), comment.getCreated());
    }

    public static List<CommentDto> mapListToCommentDto(List<Comment> comments) {
        return comments.stream().map(ItemMapper::mapCommentToDto).toList();
    }
}