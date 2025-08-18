package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private static final String BASE_URL = "/users";

    private MockMvc mvc;

    private final ObjectMapper mapper = new ObjectMapper();

    @Mock
    private UserClient userClient;

    @InjectMocks
    private UserController userController;

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto(1L, "mail@mail.ru", "Test User");
        mvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void getAllUsers() throws Exception {
        when(userClient.getAllUsers()).thenReturn(ResponseEntity.ok(List.of(userDto)));

        mvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)))
                .andExpect(content().json(mapper.writeValueAsString(List.of(userDto))));

        verify(userClient, times(1)).getAllUsers();
    }

    @Test
    void saveNewUser() throws Exception {
        when(userClient.create(any(UserDto.class))).thenReturn(ResponseEntity.ok(userDto));
        mvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(userDto)));

        verify(userClient, times(1)).create(any(UserDto.class));
    }

    @Test
    void updateUser() throws Exception {
        long userId = 1L;
        when(userClient.update(eq(userId), any(UserDto.class))).thenReturn(ResponseEntity.ok(userDto));

        mvc.perform(patch(BASE_URL + "/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(userDto)));

        verify(userClient, times(1)).update(eq(userId), any(UserDto.class));
    }

    @Test
    void deleteUser() throws Exception {
        long userId = 1L;
        when(userClient.delete(userId)).thenReturn(ResponseEntity.ok().build());

        mvc.perform(delete(BASE_URL + "/" + userId))
                .andExpect(status().isOk());

        verify(userClient, times(1)).delete(userId);
    }

    @Test
    void getUserById() throws Exception {
        long userId = 1L;
        when(userClient.getById(userId)).thenReturn(ResponseEntity.ok(userDto));

        mvc.perform(get(BASE_URL + "/" + userId))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(userDto)));

        verify(userClient, times(1)).getById(userId);
    }
}
