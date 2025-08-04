package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestCreatedDto;
import ru.practicum.shareit.request.dto.ItemRequestInputDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class ItemRequestRepositoryTest {

    @Autowired
    private ItemRequestService requestService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository requestRepository;

    @Test
    void createRequest() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");
        user = userRepository.save(user);

        ItemRequestInputDto inputDto = new ItemRequestInputDto("Нужна вещь");

        ItemRequestCreatedDto createdDto = requestService.createRequest(inputDto, user.getId());

        assertNotNull(createdDto);
        assertNotNull(createdDto.getId());
        assertEquals("Нужна вещь", createdDto.getDescription());

        boolean exists = requestRepository.existsById(createdDto.getId());
        assertTrue(exists);
    }

    @Test
    void getUserRequests() {
        User user = new User();
        user.setName("name");
        user.setEmail("email@mail.ru");
        user = userRepository.save(user);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setRequestor(user);
        itemRequest.setDescription("want something");
        itemRequest.setCreated(LocalDateTime.now());
        requestRepository.save(itemRequest);

        var result = requestService.getUserRequests(user.getId());

        assertEquals(1, result.size());
        assertEquals("want something", result.get(0).getDescription());
        assertNotNull(result.get(0).getCreated());
    }

    @Test
    void getAllRequests() {
        User user = new User();
        user.setName("name2");
        user.setEmail("email2@mail.ru");
        user = userRepository.save(user);

        ItemRequest req1 = new ItemRequest();
        req1.setRequestor(user);
        req1.setDescription("desc1");
        req1.setCreated(LocalDateTime.now().minusDays(1));
        requestRepository.save(req1);

        ItemRequest req2 = new ItemRequest();
        req2.setRequestor(user);
        req2.setDescription("desc2");
        req2.setCreated(LocalDateTime.now());
        requestRepository.save(req2);

        var result = requestService.getAllRequests();

        assertEquals(2, result.size());
        assertEquals("desc2", result.get(0).getDescription());
    }

    @Test
    void getRequestById() {
        User user = new User();
        user.setName("name3");
        user.setEmail("email3@mail.ru");
        user = userRepository.save(user);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setRequestor(user);
        itemRequest.setDescription("special request");
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest = requestRepository.save(itemRequest);

        var result = requestService.getRequestById(itemRequest.getId());

        assertNotNull(result);
        assertEquals("special request", result.getDescription());
        assertEquals(itemRequest.getId(), result.getId());
    }

    @Test
    void createRequest_whenUserNotFound_thenThrowsNotFoundException() {
        // given
        long invalidUserId = 999L;
        ItemRequestInputDto inputDto = new ItemRequestInputDto("Пустой запрос");

        // when + then
        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> requestService.createRequest(inputDto, invalidUserId));

        assertEquals("Пользователь с id=999 не найден.", ex.getMessage());
    }
}