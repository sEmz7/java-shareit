package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.InvalidUserInputException;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithDatesAndComments;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Transactional
    @Override
    public ItemDto create(ItemDto itemDto, long userId) {
        User user = findUserOrThrow(userId);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(user);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Transactional
    @Override
    public ItemDto update(ItemDto itemDto, long userId, long itemId) {
        findUserOrThrow(userId);
        Item item = findItemOrThrow(itemId);
        if (item.getOwner().getId() != userId) {
            log.warn("Редактировать вещь может только ее владелец.");
            throw new InvalidUserInputException("Редактировать вещь может только ее владелец.");
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDtoWithDatesAndComments getById(long itemId) {
        Item item = findItemOrThrow(itemId);
        List<CommentDto> commentDtos = ItemMapper.mapListToCommentDto(commentRepository.findAllByItemId(itemId));
        return ItemMapper.toItemDtoWithBookingsDatesAndComments(
                item, null, null, commentDtos);
    }

    @Override
    public List<ItemDtoWithDatesAndComments> getAllUserItems(long userId) {
        findUserOrThrow(userId);
        LocalDateTime now = LocalDateTime.now();
        List<Item> userItems = itemRepository.findAllByOwnerId(userId);
        List<Booking> bookings = bookingRepository.findAllByItemOwnerId(userId);
        Map<Item, List<Booking>> bookingsByItem = bookings.stream()
                .collect(Collectors.groupingBy(Booking::getItem));
        List<ItemDtoWithDatesAndComments> result = new ArrayList<>();
        for (Item item: userItems) {
            List<Booking> itemBookings = bookingsByItem.getOrDefault(item, List.of());
            Optional<Booking> lastBooking = itemBookings
                    .stream()
                    .filter(booking -> booking.getEnd().isBefore(now))
                    .max(Comparator.comparing(Booking::getEnd));
            Optional<Booking> nextBooking = itemBookings
                    .stream()
                    .filter(booking -> booking.getStart().isAfter(now))
                    .min(Comparator.comparing(Booking::getStart));
            List<CommentDto> commentDtos = ItemMapper.mapListToCommentDto(
                    commentRepository.findAllByItemId(item.getId())
            );
            result.add(ItemMapper.toItemDtoWithBookingsDatesAndComments(
                    item,
                    lastBooking.orElse(null),
                    nextBooking.orElse(null),
                    commentDtos
            ));
        }
        return result;
    }

    @Override
    public List<ItemDto> getAllItemsByName(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return ItemMapper.mapListToDto(itemRepository.searchAvailableItemsByNameOrDescription(text));
    }

    @Transactional
    @Override
    public CommentDto addCommentToItem(CommentDto commentDto, long itemId, long userId) {
        User user = findUserOrThrow(userId);
        Item item = findItemOrThrow(itemId);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> itemBookings = bookingRepository.findAllByItemId(itemId);
        boolean isUserBookedItem = itemBookings
                .stream()
                .anyMatch(booking -> booking.getBooker().getId() == userId && booking.getEnd().isBefore(now));
        if (!isUserBookedItem) {
            log.warn("Пользователь с id={} не бронировал вещь с id={} или бронирование еще не завершено" +
                    " — отзыв невозможен.", userId, itemId);
            throw new NotAvailableException("Отзыв возможен только после бронирования.");
        }
        Comment comment = ItemMapper.mapCommentFromDto(commentDto);
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(now);
        return ItemMapper.mapCommentToDto(commentRepository.save(comment));
    }

    private User findUserOrThrow(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> {
            log.warn("Нет пользователя с id={}", userId);
            return new NotFoundException("Нет пользователя с id=" + userId);
        });
    }

    private Item findItemOrThrow(long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> {
            log.warn("Нет вещи с id={}", itemId);
            return new NotFoundException("Нет вещи с id=" + itemId);
        });
    }
}