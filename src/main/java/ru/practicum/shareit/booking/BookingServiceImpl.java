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
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private static final String SORT_BY_START = "start";

    @Transactional
    @Override
    public BookingDtoResponse createBooking(BookingDto bookingDto, long userId) {
        User booker = getUserByIdOrThrow(userId);
        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(() -> {
            log.warn("Нет предмета с id={}", bookingDto.getItemId());
            return new NotFoundException("Нет предмета с id=" + bookingDto.getItemId());
        });
        if (!item.getAvailable()) {
            throw new NotAvailableException("Вещь недоступна для бронирования.");
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
            throw new NotAvailableException("Изменить статус бронирования может только владелец вещи.");
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
        State state = State.fromString(stateStr);
        return switch (state)  {
            case ALL -> BookingMapper.mapListToDtoResponses(
                    bookingRepository.findAllByBookerIdOrderByStartDesc(userId)
            );
            case CURRENT -> BookingMapper.mapListToDtoResponses(
                        bookingRepository.findAllByBookerIdAndStateCurrent(
                                userId, state, Sort.by(Sort.Direction.DESC, SORT_BY_START)
                        ));
            case PAST -> BookingMapper.mapListToDtoResponses(
                        bookingRepository.findAllByBookerIdAndStatePast(
                                userId, state, Sort.by(Sort.Direction.DESC, SORT_BY_START)
                        )
                );
            case FUTURE -> BookingMapper.mapListToDtoResponses(
                    bookingRepository.findAllByBookerIdAndStateFuture(
                            userId, state, Sort.by(Sort.Direction.DESC, SORT_BY_START)
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
    public List<BookingDtoResponse> getOwnerBookings(String stateStr, long ownerId) {
        State state = State.fromString(stateStr);
        List<BookingDtoResponse> bookings = switch (state) {
            case ALL -> BookingMapper.mapListToDtoResponses(
                    bookingRepository.findAllByItemOwnerIdOrderByStartDesc(ownerId));
            case CURRENT -> BookingMapper.mapListToDtoResponses(
                    bookingRepository.findAllByItemOwnerIdAndStateCurrent(
                            ownerId, state, Sort.by(Sort.Direction.DESC, SORT_BY_START)
                    )
            );
            case PAST -> BookingMapper.mapListToDtoResponses(
                    bookingRepository.findAllByItemOwnerIdAndStatePast(
                            ownerId, state, Sort.by(Sort.Direction.DESC, SORT_BY_START)
                    )
            );
            case FUTURE -> BookingMapper.mapListToDtoResponses(
                    bookingRepository.findAllByItemOwnerIdAndStateFuture(
                            ownerId, state, Sort.by(Sort.Direction.DESC, SORT_BY_START)
                    )
            );
            case WAITING -> BookingMapper.mapListToDtoResponses(
                    bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.WAITING));
            case REJECTED -> BookingMapper.mapListToDtoResponses(
                    bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.REJECTED));
        };
        if (bookings.isEmpty()) {
            throw new NotFoundException("Нету ни одной вещи для бронирования.");
        }
        return bookings;
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