package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDtoWithDatesAndComments;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class ItemServiceIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Test
    void getAllUserItems_shouldReturnItemsWithBookingsAndComments() {
        User user = new User();
        user.setName("name");
        user.setEmail("email@mail.ru");
        userRepository.save(user);

        User booker = new User();
        booker.setName("booker");
        booker.setEmail("booker@mail.ru");
        userRepository.save(booker);

        Item item = new Item();
        item.setOwner(user);
        item.setName("item1");
        item.setDescription("desc1");
        item.setAvailable(true);
        itemRepository.save(item);

        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
        booking.setStart(LocalDateTime.MIN);
        booking.setEnd(LocalDateTime.MIN.plusMinutes(10));
        bookingRepository.save(booking);

        Comment comment = new Comment();
        comment.setItem(item);
        comment.setAuthor(booker);
        comment.setCreated(LocalDateTime.MIN.plusMinutes(200));
        comment.setText("text");
        commentRepository.save(comment);

        List<ItemDtoWithDatesAndComments> result = itemService.getAllUserItems(user.getId());

        assertNotNull(result);
        assertEquals(1, result.size());

        ItemDtoWithDatesAndComments dto = result.getFirst();
        assertEquals(item.getId(), dto.getId());
        assertEquals(item.getName(), dto.getName());
        assertEquals(item.getDescription(), dto.getDescription());
        assertEquals(item.getAvailable(), dto.getAvailable());

        assertNotNull(dto.getLastBooking());
        assertEquals(booking.getStart(), dto.getLastBooking().getStart());
        assertEquals(booking.getEnd(), dto.getLastBooking().getEnd());
        assertEquals(booking.getBooker().getId(), dto.getLastBooking().getBookerId());
        assertEquals(booking.getStatus(), dto.getLastBooking().getStatus());

        assertNull(dto.getNextBooking());

        assertNotNull(dto.getComments());
        assertEquals(1, dto.getComments().size());
        assertEquals(comment.getText(), dto.getComments().getFirst().getText());
        assertEquals(booker.getName(), dto.getComments().getFirst().getAuthorName());
        assertEquals(comment.getCreated(), dto.getComments().getFirst().getCreated());
    }
}