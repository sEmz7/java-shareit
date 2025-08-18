package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithDatesAndComments;

import java.util.List;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ItemControllerTest {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemController itemController;

    private final ObjectMapper mapper = new ObjectMapper();

    private MockMvc mvc;

    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(itemController).build();
        itemDto = new ItemDto(1L, "Ball", "Ball for football", true, null);
    }

    @Test
    void createItem() throws Exception {
        when(itemService.create(any(), anyLong())).thenReturn(itemDto);

        mvc.perform(post("/items")
                    .content(mapper.writeValueAsString(itemDto))
                    .header(USER_ID_HEADER, 1L)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId())));

        verify(itemService, times(1)).create(any(), anyLong());
    }

    @Test
    void updateItem() throws Exception {
        when(itemService.update(any(), anyLong(), anyLong())).thenReturn(itemDto);

        mvc.perform(patch("/items/" + itemDto.getId())
                    .content(mapper.writeValueAsString(itemDto))
                    .header(USER_ID_HEADER, 1L)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId())));

        verify(itemService, times(1)).update(any(), anyLong(), anyLong());
    }

    @Test
    void getById() throws Exception {
        ItemDtoWithDatesAndComments itemDtoWithDatesAndComments = new ItemDtoWithDatesAndComments(1L, "1",
                "1", true, null, null, null, null);
        when(itemService.getById(anyLong())).thenReturn(itemDtoWithDatesAndComments);

        MvcResult response = mvc.perform(get("/items/" + itemDtoWithDatesAndComments.getId()))
                .andExpect(status().isOk())
                .andReturn();
        String expectedJson = mapper.writeValueAsString(itemDtoWithDatesAndComments);
        assertThatJson(response.getResponse().getContentAsString()).isEqualTo(expectedJson);

        verify(itemService, times(1)).getById(anyLong());
    }

    @Test
    void getAllUserItems() throws Exception {
        ItemDtoWithDatesAndComments itemDto1 = new ItemDtoWithDatesAndComments(1L, "1", "1",
                true, null, null, null, null);
        ItemDtoWithDatesAndComments itemDto2 = new ItemDtoWithDatesAndComments(2L, "2", "2",
                true, null, null, null, null);

        when(itemService.getAllUserItems(anyLong())).thenReturn(List.of(itemDto1, itemDto2));

        MvcResult result = mvc.perform(get("/items")
                .header(USER_ID_HEADER, 1L))
                    .andExpect(status().isOk())
                    .andReturn();

        String body = result.getResponse().getContentAsString();
        String expectedJson = mapper.writeValueAsString(List.of(itemDto1, itemDto2));
        assertThatJson(body).isEqualTo(expectedJson);

        verify(itemService, times(1)).getAllUserItems(anyLong());
    }

    @Test
    void getItemsByName() throws Exception {
        when(itemService.getAllItemsByName(anyString())).thenReturn(List.of(itemDto));

        MvcResult response = mvc.perform(get("/items/search")
                        .param("text", "for"))
                .andExpect(status().isOk())
                .andReturn();
        String expectedJson = mapper.writeValueAsString(List.of(itemDto));
        assertThatJson(response.getResponse().getContentAsString()).isEqualTo(expectedJson);

        verify(itemService, times(1)).getAllItemsByName(anyString());
    }

    @Test
    void addCommentToItem() throws Exception {
        CommentDto dto = new CommentDto(1L, "text", "name",
                null);
        when(itemService.addCommentToItem(any(), anyLong(), anyLong())).thenReturn(dto);

        MvcResult response = mvc.perform(post("/items/" + itemDto.getId() + "/comment")
                        .content(mapper.writeValueAsString(dto))
                        .header(USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String body = response.getResponse().getContentAsString();
        String expectedJson = mapper.writeValueAsString(dto);

        assertThatJson(body).isEqualTo(expectedJson);

        verify(itemService, times(1)).addCommentToItem(any(), anyLong(), anyLong());
    }
}
