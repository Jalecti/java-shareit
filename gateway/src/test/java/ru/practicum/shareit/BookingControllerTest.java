package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.exception.ErrorHandler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
@AutoConfigureMockMvc
@Import(ErrorHandler.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingClient bookingClient;

    @Test
    void create_whenInvokedWithCorrectJson_thenResponseStatusOk() throws Exception {
        Long userId = 1L;
        String json = "{" +
                "\"itemId\":1," +
                "\"start\":\"2099-06-04T01:32:56\"," +
                "\"end\":\"2100-06-04T01:32:57\"" +
                "}";

        when(bookingClient.create(any(), any())).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        ArgumentCaptor<Long> captor1 = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<NewBookingRequest> captor2 = ArgumentCaptor.forClass(NewBookingRequest.class);
        verify(bookingClient).create(captor1.capture(), captor2.capture());

        Long arg1 = captor1.getValue();
        NewBookingRequest arg2 = captor2.getValue();

        assertEquals(userId, arg1);
        assertEquals(1, arg2.getItemId());
        assertEquals("2099-06-04T01:32:56", arg2.getStart().toString());
        assertEquals("2100-06-04T01:32:57", arg2.getEnd().toString());
    }

    @Test
    void create_whenInvokedWOHeader_thenResponseStatusBadRequest() throws Exception {
        String json = "{" +
                "\"itemId\":1," +
                "\"start\":\"2099-06-04T01:32:56\"," +
                "\"end\":\"2100-06-04T01:32:57\"" +
                "}";


        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).create(any(), any());
    }

    @Test
    void create_whenInvokedWithNullItemId_thenResponseStatusBadRequest() throws Exception {
        Long userId = 1L;
        String json = "{" +
                "\"start\":\"2099-06-04T01:32:56\"," +
                "\"end\":\"2100-06-04T01:32:57\"" +
                "}";

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).create(any(), any());
    }

    @Test
    void create_whenInvokedWithNullStart_thenResponseStatusBadRequest() throws Exception {
        Long userId = 1L;
        String json = "{" +
                "\"itemId\":1," +
                "\"end\":\"2100-06-04T01:32:57\"" +
                "}";

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).create(any(), any());
    }

    @Test
    void create_whenInvokedWithPastStart_thenResponseStatusBadRequest() throws Exception {
        Long userId = 1L;
        String json = "{" +
                "\"itemId\":1," +
                "\"start\":\"2000-06-04T01:32:56\"," +
                "\"end\":\"2100-06-04T01:32:57\"" +
                "}";

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).create(any(), any());
    }

    @Test
    void create_whenInvokedWithNullEnd_thenResponseStatusBadRequest() throws Exception {
        Long userId = 1L;
        String json = "{" +
                "\"itemId\":1," +
                "\"start\":\"2000-06-04T01:32:56\"" +
                "}";

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).create(any(), any());
    }

    @Test
    void create_whenInvokedWithPastOrPresentEnd_thenResponseStatusBadRequest() throws Exception {
        Long userId = 1L;
        String json = "{" +
                "\"itemId\":1," +
                "\"start\":\"2000-06-04T01:32:56\"," +
                "\"end\":\"2025-06-04T01:32:57\"" +
                "}";

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).create(any(), any());
    }

    @Test
    void approve_whenInvoked_thenResponseStatusOk() throws Exception {
        Long userId = 1L;
        Long bookingId = 1L;
        when(bookingClient.approveBooking(any(), any(), any())).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        ArgumentCaptor<Long> captor1 = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Long> captor2 = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Boolean> captor3 = ArgumentCaptor.forClass(Boolean.class);
        verify(bookingClient).approveBooking(captor1.capture(), captor2.capture(), captor3.capture());

        Long arg1 = captor1.getValue();
        Long arg2 = captor2.getValue();
        Boolean arg3 = captor3.getValue();

        assertEquals(userId, arg1);
        assertEquals(bookingId, arg2);
        assertEquals(true, arg3);
    }

    @Test
    void approve_whenInvokedWOHeader_thenResponseStatusBadRequest() throws Exception {
        Long bookingId = 1L;

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .param("approved", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).approveBooking(any(), any(), any());
    }

    @Test
    void approve_whenInvokedWOParam_thenResponseStatusBadRequest() throws Exception {
        Long userId = 1L;
        Long bookingId = 1L;

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).approveBooking(any(), any(), any());
    }

    @Test
    void findBookingById_whenInvoked_thenResponseStatusOk() throws Exception {
        Long userId = 1L;
        Long bookingId = 1L;

        when(bookingClient.findBookingById(any(), any())).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        ArgumentCaptor<Long> captor1 = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Long> captor2 = ArgumentCaptor.forClass(Long.class);
        verify(bookingClient).findBookingById(captor1.capture(), captor2.capture());

        Long arg1 = captor1.getValue();
        Long arg2 = captor1.getValue();

        assertEquals(userId, arg1);
        assertEquals(bookingId, arg2);
    }

    @Test
    void findBookingById_whenInvokedWOHeader_thenResponseStatusBadRequest() throws Exception {
        Long bookingId = 1L;

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).findBookingById(any(), any());
    }

    @Test
    void findAllByBookerIdAndState_whenInvoked_thenResponseStatusOk() throws Exception {
        Long userId = 1L;

        when(bookingClient.findAllByBookerIdAndState(any(), any())).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "waiting")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        ArgumentCaptor<Long> captor1 = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<BookingState> captor2 = ArgumentCaptor.forClass(BookingState.class);
        verify(bookingClient).findAllByBookerIdAndState(captor1.capture(), captor2.capture());

        Long arg1 = captor1.getValue();
        BookingState arg2 = captor2.getValue();

        assertEquals(userId, arg1);
        assertEquals(BookingState.WAITING, arg2);
    }

    @Test
    void findAllByBookerIdAndState_whenInvokedWOParam_thenResponseStatusOk() throws Exception {
        Long userId = 1L;

        when(bookingClient.findAllByBookerIdAndState(any(), any())).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        ArgumentCaptor<Long> captor1 = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<BookingState> captor2 = ArgumentCaptor.forClass(BookingState.class);
        verify(bookingClient).findAllByBookerIdAndState(captor1.capture(), captor2.capture());

        Long arg1 = captor1.getValue();
        BookingState arg2 = captor2.getValue();

        assertEquals(userId, arg1);
        assertEquals(BookingState.ALL, arg2);
    }

    @Test
    void findAllByBookerIdAndState_whenInvokedWithIncorrectState_thenResponseStatusBadRequest() throws Exception {
        Long userId = 1L;

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "???")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).findAllByBookerIdAndState(any(), any());
    }

    @Test
    void findAllByBookerIdAndState_whenInvokedWOHeader_thenResponseStatusBadRequest() throws Exception {
        mockMvc.perform(get("/bookings")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).findAllByBookerIdAndState(any(), any());
    }

    @Test
    void findAllByOwnerIdAndState_whenInvoked_thenResponseStatusOk() throws Exception {
        Long userId = 1L;

        when(bookingClient.findAllByOwnerIdAndState(any(), any())).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "waiting")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        ArgumentCaptor<Long> captor1 = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<BookingState> captor2 = ArgumentCaptor.forClass(BookingState.class);
        verify(bookingClient).findAllByOwnerIdAndState(captor1.capture(), captor2.capture());

        Long arg1 = captor1.getValue();
        BookingState arg2 = captor2.getValue();

        assertEquals(userId, arg1);
        assertEquals(BookingState.WAITING, arg2);
    }

    @Test
    void findAllByOwnerIdAndState_whenInvokedWOParam_thenResponseStatusOk() throws Exception {
        Long userId = 1L;

        when(bookingClient.findAllByOwnerIdAndState(any(), any())).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        ArgumentCaptor<Long> captor1 = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<BookingState> captor2 = ArgumentCaptor.forClass(BookingState.class);
        verify(bookingClient).findAllByOwnerIdAndState(captor1.capture(), captor2.capture());

        Long arg1 = captor1.getValue();
        BookingState arg2 = captor2.getValue();

        assertEquals(userId, arg1);
        assertEquals(BookingState.ALL, arg2);
    }

    @Test
    void findAllByOwnerIdAndState_whenInvokedWithIncorrectState_thenResponseStatusBadRequest() throws Exception {
        Long userId = 1L;

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "???")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).findAllByOwnerIdAndState(any(), any());
    }

    @Test
    void findAllByOwnerIdAndState_whenInvokedWOHeader_thenResponseStatusBadRequest() throws Exception {
        mockMvc.perform(get("/bookings/owner")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).findAllByOwnerIdAndState(any(), any());
    }
}