package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.InvalidUserInputException;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithDatesAndComments;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceUnitTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void getAllItemsByName_whenTextIsBlank_shouldReturnEmptyList() {
        List<ItemDto> result = itemService.getAllItemsByName("  ");
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verifyNoInteractions(itemRepository);
    }

    @Test
    void getAllItemsByName_whenTextIsValid_shouldReturnList() {
        String text = "дрель";
        Item item = new Item();
        item.setId(1L);
        item.setName("дрель");
        item.setDescription("мощная дрель");
        item.setAvailable(true);

        when(itemRepository.searchAvailableItemsByNameOrDescription(text)).thenReturn(List.of(item));

        List<ItemDto> result = itemService.getAllItemsByName(text);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("дрель", result.getFirst().getName());
        verify(itemRepository).searchAvailableItemsByNameOrDescription(text);
    }

    @Test
    void getById_whenItemExists_shouldReturnItemDtoWithComments() {
        long itemId = 1L;
        Item item = new Item();
        item.setId(itemId);
        item.setName("дрель");

        User author = new User();
        author.setId(2L);
        author.setName("Автор");

        Comment comment = new Comment();
        comment.setText("отлично работает");
        comment.setAuthor(author);
        comment.setItem(item);


        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemId(itemId)).thenReturn(List.of(comment));

        ItemDtoWithDatesAndComments result = itemService.getById(itemId);

        assertNotNull(result);
        assertEquals(itemId, result.getId());
        assertEquals("дрель", result.getName());
        assertEquals(1, result.getComments().size());
        assertEquals("отлично работает", result.getComments().getFirst().getText());

        verify(itemRepository).findById(itemId);
        verify(commentRepository).findAllByItemId(itemId);
    }

    @Test
    void getById_whenItemNotFound_shouldThrowException() {
        long itemId = 999L;

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> itemService.getById(itemId));

        assertTrue(ex.getMessage().contains("Нет вещи с id="));
        verify(itemRepository).findById(itemId);
        verifyNoInteractions(commentRepository);
    }

    @Test
    void update_whenUserIsOwner_shouldUpdateItem() {
        long userId = 1L;
        long itemId = 100L;

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Updated name");
        itemDto.setDescription("Updated desc");
        itemDto.setAvailable(false);

        User owner = new User();
        owner.setId(userId);

        Item existingItem = new Item();
        existingItem.setId(itemId);
        existingItem.setOwner(owner);
        existingItem.setName("Old name");
        existingItem.setDescription("Old desc");
        existingItem.setAvailable(true);

        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));
        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ItemDto result = itemService.update(itemDto, userId, itemId);

        assertNotNull(result);
        assertEquals("Updated name", result.getName());
        assertEquals("Updated desc", result.getDescription());
        assertFalse(result.getAvailable());

        verify(itemRepository).save(existingItem);
    }

    @Test
    void update_whenUserNotOwner_shouldThrowException() {
        long userId = 1L;
        long itemId = 2L;

        User owner = new User();
        owner.setId(999L);

        Item item = new Item();
        item.setId(itemId);
        item.setOwner(owner);

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(InvalidUserInputException.class, () -> itemService.update(new ItemDto(), userId, itemId));
    }

    @Test
    void update_whenSomeFieldsNull_shouldOnlyUpdateSetFields() {
        long userId = 1L;
        long itemId = 3L;

        ItemDto itemDto = new ItemDto();
        itemDto.setName("New name");

        User owner = new User();
        owner.setId(userId);

        Item existingItem = new Item();
        existingItem.setId(itemId);
        existingItem.setOwner(owner);
        existingItem.setName("Old name");
        existingItem.setDescription("Old desc");
        existingItem.setAvailable(true);

        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));
        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ItemDto result = itemService.update(itemDto, userId, itemId);

        assertEquals("New name", result.getName());
        assertEquals("Old desc", result.getDescription());
        assertTrue(result.getAvailable());
    }

    @Test
    void addCommentToItem_whenUserBookedAndBookingEnded_shouldSaveComment() {
        long userId = 1L;
        long itemId = 2L;

        User user = new User();
        user.setId(userId);

        Item item = new Item();
        item.setId(itemId);

        Booking booking = new Booking();
        booking.setBooker(user);
        booking.setEnd(LocalDateTime.now().minusDays(1));

        CommentDto inputDto = new CommentDto();
        inputDto.setText("Комментарий");

        Comment savedComment = new Comment();
        savedComment.setText("Комментарий");
        savedComment.setItem(item);
        savedComment.setAuthor(user);
        savedComment.setCreated(LocalDateTime.now());

        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(java.util.Optional.of(item));
        when(bookingRepository.findAllByItemId(itemId)).thenReturn(List.of(booking));
        when(commentRepository.save(any(Comment.class))).thenReturn(savedComment);

        CommentDto result = itemService.addCommentToItem(inputDto, itemId, userId);

        assertNotNull(result);
        assertEquals("Комментарий", result.getText());

        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void addCommentToItem_whenUserHasNoFinishedBooking_shouldThrowException() {
        long userId = 1L;
        long itemId = 2L;

        User user = new User();
        user.setId(userId);

        Item item = new Item();
        item.setId(itemId);

        Booking booking = new Booking();
        booking.setBooker(user);
        booking.setEnd(LocalDateTime.now().plusDays(1));

        CommentDto inputDto = new CommentDto();
        inputDto.setText("Комментарий");

        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(java.util.Optional.of(item));
        when(bookingRepository.findAllByItemId(itemId)).thenReturn(List.of(booking));

        assertThrows(NotAvailableException.class, () -> itemService.addCommentToItem(inputDto, itemId, userId));

        verify(commentRepository, never()).save(any());
    }
}