package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDtoShort;
import ru.practicum.shareit.user.dto.UserDtoShort;

import java.util.List;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class BookingControllerTest {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    private final ObjectMapper mapper = new ObjectMapper();

    private MockMvc mvc;

    private BookingDto bookingDto;

    private BookingDtoResponse bookingDtoResponse;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(bookingController).build();
        bookingDto = new BookingDto(1L, null, null, 1L, 1L, "WAITING");
        bookingDtoResponse = new BookingDtoResponse(1L, null, null, new ItemDtoShort(1L, "item"),
                new UserDtoShort(1L, "user"), BookingStatus.WAITING);
    }

    @Test
    void createBooking() throws Exception {
        when(bookingService.createBooking(any(), anyLong())).thenReturn(bookingDtoResponse);

        MvcResult response = mvc.perform(post("/bookings")
                    .content(mapper.writeValueAsString(bookingDto))
                    .header(USER_ID_HEADER, 1L)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String body = response.getResponse().getContentAsString();
        String expectedJson = mapper.writeValueAsString(bookingDtoResponse);

        assertThatJson(body).isEqualTo(expectedJson);

        verify(bookingService, times(1)).createBooking(any(), anyLong());
    }

    @Test
    void updateBooking() throws Exception {
        when(bookingService.updateBookingStatus(anyLong(), anyBoolean(), anyLong())).thenReturn(bookingDtoResponse);

        MvcResult response = mvc.perform(patch("/bookings/" + bookingDto.getId())
                    .param("approved", "true")
                    .header(USER_ID_HEADER, 1L)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String body = response.getResponse().getContentAsString();
        String expectedJson = mapper.writeValueAsString(bookingDtoResponse);

        assertThatJson(body).isEqualTo(expectedJson);

        verify(bookingService, times(1)).updateBookingStatus(anyLong(), anyBoolean(), anyLong());
    }

    @Test
    void getBookingById() throws Exception {
        when(bookingService.getBookingById(anyLong())).thenReturn(bookingDtoResponse);

        MvcResult response = mvc.perform(get("/bookings/" + bookingDto.getId())
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andReturn();

        String body = response.getResponse().getContentAsString();
        String expectedJson = mapper.writeValueAsString(bookingDtoResponse);

        assertThatJson(body).isEqualTo(expectedJson);

        verify(bookingService, times(1)).getBookingById(anyLong());
    }

    @Test
    void getUserBookings() throws Exception {
        when(bookingService.getUserBookings(anyString(), anyLong()))
                .thenReturn(List.of(bookingDtoResponse));

        MvcResult response = mvc.perform(get("/bookings")
                        .param("state", "ALL")
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andReturn();

        String body = response.getResponse().getContentAsString();
        String expectedJson = mapper.writeValueAsString(List.of(bookingDtoResponse));

        assertThatJson(body).isEqualTo(expectedJson);

        verify(bookingService, times(1)).getUserBookings(anyString(), anyLong());
    }

    @Test
    void getOwnerBookings() throws Exception {
        when(bookingService.getOwnerBookings(anyString(), anyLong()))
                .thenReturn(List.of(bookingDtoResponse));

        MvcResult response = mvc.perform(get("/bookings/owner")
                        .param("state", "ALL")
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andReturn();

        String body = response.getResponse().getContentAsString();
        String expectedJson = mapper.writeValueAsString(List.of(bookingDtoResponse));

        assertThatJson(body).isEqualTo(expectedJson);

        verify(bookingService, times(1)).getOwnerBookings(anyString(), anyLong());
    }
}