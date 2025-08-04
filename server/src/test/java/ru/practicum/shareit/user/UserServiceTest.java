package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
public class UserServiceTest {

    @Autowired
    private UserService userService;

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto(null, "mail@mail.ru", "name");
    }

    @Test
    void createAndFindUser() {
        UserDto result = userService.saveUser(userDto);

        assertNotNull(result);

        UserDto foundUser = userService.getUserById(result.getId());
        assertThat(foundUser.getName(), is(userDto.getName()));
    }

    @Test
    void updateAndFindUser() {
        UserDto savedUser = userService.saveUser(userDto);
        savedUser.setEmail("newEmail@mail.ru");
        userService.updateUser(savedUser.getId(), savedUser);

        UserDto foundUser = userService.getUserById(savedUser.getId());
        assertThat(foundUser.getEmail(), is(savedUser.getEmail()));
    }

    @Test
    void deleteUser() {
        UserDto savedUser = userService.saveUser(userDto);
        userService.deleteUser(savedUser.getId());
        assertThrows(NotFoundException.class, () -> userService.getUserById(savedUser.getId()));
    }

    @Test
    void deleteAlreadyDeleatedUser() {
        UserDto savedUser = userService.saveUser(userDto);
        userService.deleteUser(savedUser.getId());
        assertThrows(NotFoundException.class, () -> userService.deleteUser(savedUser.getId()));
    }

    @Test
    void getAllUsers() {
        UserDto savedUser = userService.saveUser(userDto);
        UserDto savedUser2 = new UserDto();
        savedUser2.setEmail("new@mail.ru");
        savedUser2.setName("name2");
        savedUser2 = userService.saveUser(savedUser2);

        List<UserDto> users = userService.getAllUsers();
        assertNotNull(users);
        assertThat(users.size(), is(2));
        assertThat(users, hasItems(savedUser, savedUser2));
    }
}