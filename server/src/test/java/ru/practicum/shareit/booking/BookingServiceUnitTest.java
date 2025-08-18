package ru.practicum.shareit.booking;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.booking.dto.State;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class BookingServiceUnitTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private final long userId = 1L;
    private final long ownerId = 1L;

    @Test
    void getBookingById_whenBookingExists_shouldReturnBookingDtoResponse() {
        User user = new User(1L, "user@mail.ru", "name");
        User booker = new User(2L, "booker@mail.ru", "name");
        Item item = new Item(1L, "ball", "ball for football", true, user, null);
        long bookingId = 1L;

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setItem(item);
        booking.setBooker(booker);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        BookingDtoResponse result = bookingService.getBookingById(bookingId);

        assertNotNull(result);
        assertEquals(bookingId, result.getId());

        verify(bookingRepository).findById(bookingId);
    }

    @Test
    void getBookingById_whenBookingNotFound_shouldThrowNotFoundException() {
        long bookingId = 1L;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.getBookingById(bookingId));

        verify(bookingRepository).findById(bookingId);
    }

    @Test
    void updateBookingStatus_whenUserIsNotOwner_shouldThrowNotAvailableException() {
        long bookingId = 1L;
        long userId = 10L;

        User owner = new User(2L, "owner@mail.ru", "Owner");
        User booker = new User(3L, "booker@mail.ru", "Booker");
        Item item = new Item(1L, "Item", "Desc", true, owner, null);

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setItem(item);
        booking.setBooker(booker);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(NotAvailableException.class,
                () -> bookingService.updateBookingStatus(bookingId, true, userId));

        verify(bookingRepository).findById(bookingId);
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    void updateBookingStatus_whenUserIsOwner_andApprovedTrue_shouldSetStatusApproved() {
        long bookingId = 1L;
        long userId = 2L;

        User owner = new User(userId, "owner@mail.ru", "Owner");
        User booker = new User(3L, "booker@mail.ru", "Booker");
        Item item = new Item(1L, "Item", "Desc", true, owner, null);

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));

        BookingDtoResponse result = bookingService.updateBookingStatus(bookingId, true, userId);

        assertNotNull(result);
        assertEquals(bookingId, result.getId());
        assertEquals(BookingStatus.APPROVED, booking.getStatus());

        verify(bookingRepository).findById(bookingId);
        verify(userRepository).findById(userId);
    }

    @Test
    void updateBookingStatus_whenUserIsOwner_andApprovedFalse_shouldSetStatusRejected() {
        long bookingId = 1L;
        long userId = 2L;

        User owner = new User(userId, "owner@mail.ru", "Owner");
        User booker = new User(3L, "booker@mail.ru", "Booker");
        Item item = new Item(1L, "Item", "Desc", true, owner, null);

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));

        BookingDtoResponse result = bookingService.updateBookingStatus(bookingId, false, userId);

        assertNotNull(result);
        assertEquals(bookingId, result.getId());
        assertEquals(BookingStatus.REJECTED, booking.getStatus());

        verify(bookingRepository).findById(bookingId);
        verify(userRepository).findById(userId);
    }

    @Test
    void getUserBookings_whenStateAll_shouldCallFindAllByBookerIdOrderByStartDesc() {
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(userId))
                .thenReturn(Collections.emptyList());

        List<BookingDtoResponse> result = bookingService.getUserBookings("ALL", userId);

        assertNotNull(result);
        verify(bookingRepository).findAllByBookerIdOrderByStartDesc(userId);
    }

    @Test
    void getUserBookings_whenStateCurrent_shouldCallFindAllByBookerIdAndStateCurrent() {
        when(bookingRepository.findAllByBookerIdAndStateCurrent(
                eq(userId), eq(State.CURRENT), any(Sort.class)))
                .thenReturn(Collections.emptyList());

        List<BookingDtoResponse> result = bookingService.getUserBookings("CURRENT", userId);

        assertNotNull(result);
        verify(bookingRepository).findAllByBookerIdAndStateCurrent(
                eq(userId), eq(State.CURRENT), any(Sort.class));
    }

    @Test
    void getUserBookings_whenStatePast_shouldCallFindAllByBookerIdAndStatePast() {
        when(bookingRepository.findAllByBookerIdAndStatePast(
                eq(userId), eq(State.PAST), any(Sort.class)))
                .thenReturn(Collections.emptyList());

        List<BookingDtoResponse> result = bookingService.getUserBookings("PAST", userId);

        assertNotNull(result);
        verify(bookingRepository).findAllByBookerIdAndStatePast(
                eq(userId), eq(State.PAST), any(Sort.class));
    }

    @Test
    void getUserBookings_whenStateFuture_shouldCallFindAllByBookerIdAndStateFuture() {
        when(bookingRepository.findAllByBookerIdAndStateFuture(
                eq(userId), eq(State.FUTURE), any(Sort.class)))
                .thenReturn(Collections.emptyList());

        List<BookingDtoResponse> result = bookingService.getUserBookings("FUTURE", userId);

        assertNotNull(result);
        verify(bookingRepository).findAllByBookerIdAndStateFuture(
                eq(userId), eq(State.FUTURE), any(Sort.class));
    }

    @Test
    void getUserBookings_whenStateWaiting_shouldCallFindAllByBookerIdAndStatusOrderByStartDescWithWaiting() {
        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(
                userId, BookingStatus.WAITING))
                .thenReturn(Collections.emptyList());

        List<BookingDtoResponse> result = bookingService.getUserBookings("WAITING", userId);

        assertNotNull(result);
        verify(bookingRepository).findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
    }

    @Test
    void getUserBookings_whenStateRejected_shouldCallFindAllByBookerIdAndStatusOrderByStartDescWithRejected() {
        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(
                userId, BookingStatus.REJECTED))
                .thenReturn(Collections.emptyList());

        List<BookingDtoResponse> result = bookingService.getUserBookings("REJECTED", userId);

        assertNotNull(result);
        verify(bookingRepository).findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
    }

    @Test
    void getOwnerBookings_whenStateAll_shouldReturnBookingDtoResponseList() {
        when(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(ownerId))
                .thenReturn(List.of(mockBookingEntity()));

        List<BookingDtoResponse> result = bookingService.getOwnerBookings("ALL", ownerId);

        assertNotNull(result);
        assertFalse(result.isEmpty());

        verify(bookingRepository).findAllByItemOwnerIdOrderByStartDesc(ownerId);
    }

    @Test
    void getOwnerBookings_whenStateWaiting_shouldReturnBookingDtoResponseList() {
        when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.WAITING))
                .thenReturn(List.of(mockBookingEntity()));

        List<BookingDtoResponse> result = bookingService.getOwnerBookings("WAITING", ownerId);

        assertNotNull(result);
        assertFalse(result.isEmpty());

        verify(bookingRepository).findAllByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.WAITING);
    }

    @Test
    void getOwnerBookings_whenNoBookingsFound_shouldThrowNotFoundException() {
        when(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(ownerId))
                .thenReturn(List.of());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> bookingService.getOwnerBookings("ALL", ownerId));
        assertEquals("Нету ни одной вещи для бронирования.", ex.getMessage());

        verify(bookingRepository).findAllByItemOwnerIdOrderByStartDesc(ownerId);
    }

    @Test
    void getOwnerBookings_whenStatePast_shouldReturnBookingDtoResponseList() {
        when(bookingRepository.findAllByItemOwnerIdAndStatePast(eq(ownerId), any(), any(Sort.class)))
                .thenReturn(List.of(mockBookingEntity()));

        List<BookingDtoResponse> result = bookingService.getOwnerBookings("PAST", ownerId);

        assertNotNull(result);
        assertFalse(result.isEmpty());

        verify(bookingRepository).findAllByItemOwnerIdAndStatePast(eq(ownerId), any(), any(Sort.class));
    }

    private Booking mockBookingEntity() {
        User owner = new User(userId, "owner@mail.ru", "Owner");
        User booker = new User(3L, "booker@mail.ru", "Booker");
        Item item = new Item(1L, "Item", "Desc", true, owner, null);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setItem(item);
        booking.setBooker(booker);

        return booking;
    }
}