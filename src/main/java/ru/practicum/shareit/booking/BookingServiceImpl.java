package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.InvalidUserInputException;
import ru.practicum.shareit.exception.NotAvailable;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional
    @Override
    public BookingDtoResponse createBooking(BookingDto bookingDto, long userId) {
        User booker = getUserByIdOrThrow(userId);
        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(() -> {
            log.warn("Нет предмета с id={}", bookingDto.getItemId());
            return new NotFoundException("Нет предмета с id=" + bookingDto.getItemId());
        });
        if (!item.getAvailable()) {
            throw new NotAvailable("Вещь недоступна для бронирования.");
        }
        Booking booking = BookingMapper.mapFromDto(bookingDto);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        return BookingMapper.mapToDtoResponse(bookingRepository.save(booking));
    }

    @Transactional
    @Override
    public BookingDtoResponse updateBookingStatus(long bookingId, boolean approved, long userId) {
        Booking booking = getBookingByIdOrThrow(bookingId);
        if (booking.getItem().getOwner().getId() != userId) {
            throw new NotAvailable("Изменить статус бронирования может только владелец вещи.");
        }
        getUserByIdOrThrow(userId);
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return BookingMapper.mapToDtoResponse(booking);
    }

    @Override
    public BookingDtoResponse getBookingById(long bookingId) {
        return BookingMapper.mapToDtoResponse(getBookingByIdOrThrow(bookingId));
    }

    @Override
    public List<BookingDtoResponse> getUserBookings(String stateStr, long userId) {
        State state;
        try {
            state = State.valueOf(stateStr);
        } catch (IllegalArgumentException e) {
            throw new InvalidUserInputException("Нет такого статуса брони: " + stateStr);
        }
        return switch (state)  {
            case ALL -> BookingMapper.mapListToDtoResponses(
                    bookingRepository.findAllByBookerIdOrderByStartDesc(userId)
            );
            case CURRENT -> BookingMapper.mapListToDtoResponses(
                        bookingRepository.findAllByBookerIdAndStateCurrent(
                                userId, state, Sort.by(Sort.Direction.DESC, "start")
                        ));
            case PAST -> BookingMapper.mapListToDtoResponses(
                        bookingRepository.findAllByBookerIdAndStatePast(
                                userId, state, Sort.by(Sort.Direction.DESC, "start")
                        )
                );
            case FUTURE -> BookingMapper.mapListToDtoResponses(
                    bookingRepository.findAllByBookerIdAndStateFuture(
                            userId, state, Sort.by(Sort.Direction.DESC, "start")
                    )
            );
            case WAITING -> BookingMapper.mapListToDtoResponses(
                            bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING)
                );
            case REJECTED -> BookingMapper.mapListToDtoResponses(
                    bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED)
                );
            };
    }

    @Override
    public List<BookingDtoResponse> getOwnerBookings(String state, long ownerId) {
        if (state.equals("ALL")) {
            return BookingMapper.mapListToDtoResponses(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(ownerId));
        }
        BookingStatus status;
        try {
            status = BookingStatus.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidUserInputException("Нет такого статуса брони: " + state);
        }
        return BookingMapper.mapListToDtoResponses(
                bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(ownerId, status)
        );
    }

    private User getUserByIdOrThrow(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> {
            log.warn("Нет пользователя с id={}", userId);
            return new NotFoundException("Нет пользователя с id=" + userId);
        });
    }

    private Booking getBookingByIdOrThrow(long id) {
        return bookingRepository.findById(id).orElseThrow(() -> {
            log.warn("Нет бронирования с id={}", id);
            return new NotFoundException("Нет бронирования с id=" + id);
        });
    }
}