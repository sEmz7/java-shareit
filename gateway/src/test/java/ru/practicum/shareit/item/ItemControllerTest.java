package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ItemControllerTest {

    private static final String BASE_URL = "/items";

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private MockMvc mvc;

    private final ObjectMapper mapper = new ObjectMapper();

    @Mock
    private ItemClient itemClient;

    @InjectMocks
    private ItemController itemController;

    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(itemController).build();
        itemDto = new ItemDto(1L, "name", "description", true, null);
    }

    @Test
    void createItem() throws Exception {
        when(itemClient.create(any(), anyLong())).thenReturn(ResponseEntity.ok(itemDto));

        mvc.perform(post(BASE_URL)
                    .content(mapper.writeValueAsString(itemDto))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemDto)));

        verify(itemClient, times(1)).create(any(), anyLong());
    }

    @Test
    void updateItem() throws Exception {
        when(itemClient.update(any(), anyLong(), anyLong())).thenReturn(ResponseEntity.ok(itemDto));

        mvc.perform(patch(BASE_URL + "/1")
                        .content(mapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemDto)));

        verify(itemClient, times(1)).update(itemDto, 1L, itemDto.getId());
    }

    @Test
    void getItemById() throws Exception {
        when(itemClient.getById(1L)).thenReturn(ResponseEntity.ok(itemDto));

        mvc.perform(get(BASE_URL + "/" + itemDto.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemDto)));

        verify(itemClient, times(1)).getById(itemDto.getId());
    }

    @Test
    void getAllUserItems() throws Exception {
        when(itemClient.getAllUserItems(1L)).thenReturn(ResponseEntity.ok(List.of(itemDto)));

        mvc.perform(get(BASE_URL)
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)))
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemDto))));

        verify(itemClient, times(1)).getAllUserItems(1L);
    }

    @Test
    void getItemsByName() throws Exception {
        when(itemClient.getItemsByName("item")).thenReturn(ResponseEntity.ok(List.of(itemDto)));

        mvc.perform(get(BASE_URL + "/search")
                        .param("text", "item"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)))
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemDto))));

        verify(itemClient, times(1)).getItemsByName("item");
    }

    @Test
    void addCommentToItem() throws Exception {
        CommentDto commentDto = new CommentDto(1L, "Great item!", "Author", null);

        when(itemClient.addComment(any(), eq(1L), eq(1L))).thenReturn(ResponseEntity.ok(commentDto));

        mvc.perform(post(BASE_URL + "/1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentDto))
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(commentDto)));

        verify(itemClient, times(1)).addComment(any(), eq(1L), eq(1L));
    }
}