package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
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
public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private final ObjectMapper mapper = new ObjectMapper();

    private MockMvc mvc;

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(userController).build();
        userDto = new UserDto(1L, "semzz@mail.ru", "Semyon");
    }

    @Test
    void saveNewUser() throws Exception {
        when(userService.saveUser(any())).thenReturn(userDto);

        mvc.perform(post("/users")
                    .content(mapper.writeValueAsString(userDto))
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));

        verify(userService, times(1)).saveUser(any());
    }

    @Test
    void updateUser() throws Exception {
        when(userService.updateUser(anyLong(), any())).thenReturn(userDto);

        mvc.perform(patch("/users/" + userDto.getId())
                    .content(mapper.writeValueAsString(userDto))
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));

        verify(userService, times(1)).updateUser(anyLong(), any());
    }

    @Test
    void deleteUser() throws Exception {
        mvc.perform(delete("/users/" + userDto.getId()))
                .andExpect(status().is(204));

        verify(userService, times(1)).deleteUser(anyLong());
    }

    @Test
    void getUserById() throws Exception {
        when(userService.getUserById(userDto.getId())).thenReturn(userDto);

        mvc.perform(get("/users/" + userDto.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));

        verify(userService, times(1)).getUserById(userDto.getId());
    }

    @Test
    void getAllUsers() throws Exception {
        UserDto userDto2 = new UserDto(2L, "second@mail.ru", "second");
        UserDto userDto3 = new UserDto(3L, "third@mail.ru", "third");

        when(userService.getAllUsers()).thenReturn(List.of(
                userDto,
                userDto2,
                userDto3));

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(3)))
                .andExpect(content().json(mapper.writeValueAsString(List.of(
                        userDto,
                        userDto2,
                        userDto3
                ))));

        verify(userService, times(1)).getAllUsers();
    }
}
