package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.dto.BookingDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    private static final String BASE_URL = "/bookings";
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private MockMvc mvc;
    private final ObjectMapper mapper = new ObjectMapper();

    @Mock
    private BookingClient bookingClient;

    @InjectMocks
    private BookingController bookingController;

    private BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        mapper.registerModule(new JavaTimeModule());
        mvc = MockMvcBuilders.standaloneSetup(bookingController).build();
        bookingDto = new BookingDto(1L, LocalDateTime.MIN, LocalDateTime.MAX, 1L, 1L,
                "WAITING");
    }

    @Test
    void createBooking() throws Exception {
        when(bookingClient.createBooking(any(), anyLong())).thenReturn(ResponseEntity.ok(bookingDto));

        mvc.perform(post(BASE_URL)
                        .header(USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDto)));

        verify(bookingClient, times(1)).createBooking(any(), anyLong());
    }

    @Test
    void updateBooking() throws Exception {
        when(bookingClient.update(eq(1L), eq(1L), eq(true))).thenReturn(ResponseEntity.ok(bookingDto));

        mvc.perform(patch(BASE_URL + "/1")
                        .header(USER_ID_HEADER, 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDto)));

        verify(bookingClient, times(1)).update(1L, 1L, true);
    }

    @Test
    void getBookingById() throws Exception {
        when(bookingClient.getBookingById(1L)).thenReturn(ResponseEntity.ok(bookingDto));

        mvc.perform(get(BASE_URL + "/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDto)));

        verify(bookingClient, times(1)).getBookingById(1L);
    }

    @Test
    void getUserBookings() throws Exception {
        when(bookingClient.getUserBookings("ALL", 1L)).thenReturn(ResponseEntity.ok(List.of(bookingDto)));

        mvc.perform(get(BASE_URL)
                        .header(USER_ID_HEADER, 1L)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)))
                .andExpect(content().json(mapper.writeValueAsString(List.of(bookingDto))));

        verify(bookingClient, times(1)).getUserBookings("ALL", 1L);
    }

    @Test
    void getOwnerBookings() throws Exception {
        when(bookingClient.getOwnerBookings("ALL", 1L)).thenReturn(ResponseEntity.ok(List.of(bookingDto)));

        mvc.perform(get(BASE_URL + "/owner")
                        .header(USER_ID_HEADER, 1L)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)))
                .andExpect(content().json(mapper.writeValueAsString(List.of(bookingDto))));

        verify(bookingClient, times(1)).getOwnerBookings("ALL", 1L);
    }
}
