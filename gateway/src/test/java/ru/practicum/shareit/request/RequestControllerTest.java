package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import ru.practicum.shareit.request.dto.ItemRequestCreatedDto;
import ru.practicum.shareit.request.dto.ItemRequestGetDto;
import ru.practicum.shareit.request.dto.ItemRequestInputDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class RequestControllerTest {

    private static final String BASE_URL = "/requests";
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @Mock
    private RequestClient requestClient;

    @InjectMocks
    private RequestController requestController;

    private MockMvc mvc;

    private final ObjectMapper mapper = new ObjectMapper();

    private ItemRequestCreatedDto createdDto;

    private ItemRequestGetDto getDto;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(requestController).build();
        mapper.registerModule(new JavaTimeModule());
        createdDto = new ItemRequestCreatedDto(1L, "want something", LocalDateTime.MAX);
        getDto = new ItemRequestGetDto(1L, "want something", LocalDateTime.MAX, null);
    }

    @Test
    void createRequest() throws Exception {
        ItemRequestInputDto inputDto = new ItemRequestInputDto("want something");
        when(requestClient.create(any(), anyLong())).thenReturn(ResponseEntity.ok(createdDto));

        mvc.perform(post(BASE_URL)
                    .content(mapper.writeValueAsString(inputDto))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(createdDto)));

        verify(requestClient, times(1)).create(any(), anyLong());
    }

    @Test
    void getUserRequests() throws Exception {
        when(requestClient.getUserRequests(anyLong())).thenReturn(ResponseEntity.ok(List.of(getDto)));

        mvc.perform(get(BASE_URL)
                    .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(getDto))));

        verify(requestClient, times(1)).getUserRequests(anyLong());
    }

    @Test
    void getAllRequests() throws Exception {
        when(requestClient.getAllRequests()).thenReturn(ResponseEntity.ok(List.of(getDto)));

        mvc.perform(get(BASE_URL + "/all")
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(getDto))));

        verify(requestClient, times(1)).getAllRequests();
    }

    @Test
    void getRequestInfoById() throws Exception {
        long requestId = 1L;
        when(requestClient.getRequestInfoById(requestId)).thenReturn(ResponseEntity.ok(getDto));

        mvc.perform(get(BASE_URL + "/" + requestId)
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(getDto)));

        verify(requestClient, times(1)).getRequestInfoById(requestId);
    }
}