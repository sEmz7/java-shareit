package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestCreatedDto;
import ru.practicum.shareit.request.dto.ItemRequestInputDto;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

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
}

