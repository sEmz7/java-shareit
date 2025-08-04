package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.request.dto.ItemRequestCreatedDto;
import ru.practicum.shareit.request.dto.ItemRequestGetDto;
import ru.practicum.shareit.request.dto.ItemRequestInputDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTest {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @Mock
    private ItemRequestService requestService;

    @InjectMocks
    private ItemRequestController controller;

    private MockMvc mvc;
    private final ObjectMapper mapper = new ObjectMapper();

    private ItemRequestInputDto inputDto;
    private ItemRequestCreatedDto createdDto;
    private ItemRequestGetDto getDto;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(controller).build();

        inputDto = new ItemRequestInputDto("Нужен ноутбук");
        createdDto = new ItemRequestCreatedDto(1L, "Нужен ноутбук", null);
        getDto = new ItemRequestGetDto(1L, "Нужен ноутбук", null, List.of());
    }

    @Test
    void createRequest() throws Exception {
        when(requestService.createRequest(any(), anyLong())).thenReturn(createdDto);

        MvcResult result = mvc.perform(post("/requests")
                        .header(USER_ID_HEADER, 1L)
                        .content(mapper.writeValueAsString(inputDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String body = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertThatJson(body).isEqualTo(mapper.writeValueAsString(createdDto));

        verify(requestService, times(1)).createRequest(any(), anyLong());
    }

    @Test
    void getUserRequests() throws Exception {
        when(requestService.getUserRequests(anyLong())).thenReturn(List.of(getDto));

        MvcResult result = mvc.perform(get("/requests")
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andReturn();

        String body = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertThatJson(body).isEqualTo(mapper.writeValueAsString(List.of(getDto)));

        verify(requestService, times(1)).getUserRequests(anyLong());
    }

    @Test
    void getAllRequests() throws Exception {
        when(requestService.getAllRequests()).thenReturn(List.of(getDto));

        MvcResult result = mvc.perform(get("/requests/all"))
                .andExpect(status().isOk())
                .andReturn();

        String body = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertThatJson(body).isEqualTo(mapper.writeValueAsString(List.of(getDto)));

        verify(requestService, times(1)).getAllRequests();
    }

    @Test
    void getRequestInfoById() throws Exception {
        when(requestService.getRequestById(anyLong())).thenReturn(getDto);

        MvcResult result = mvc.perform(get("/requests/" + getDto.getId()))
                .andExpect(status().isOk())
                .andReturn();

        String body = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertThatJson(body).isEqualTo(mapper.writeValueAsString(getDto));

        verify(requestService, times(1)).getRequestById(anyLong());
    }
}