package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.ItemRequestClient;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.NewItemRequestRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(controllers = ItemRequestController.class)
@AutoConfigureMockMvc
class ItemRequestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestClient itemRequestClient;

    @Test
    void create_whenInvokedWithCorrectJson_thenResponseStatusOk() throws Exception {
        Long userId = 1L;
        String json = "{" +
                "\"description\":\"description1\"" +
                "}";

        when(itemRequestClient.create(any(), any())).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        ArgumentCaptor<Long> captor1 = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<NewItemRequestRequest> captor2 = ArgumentCaptor.forClass(NewItemRequestRequest.class);
        verify(itemRequestClient).create(captor1.capture(), captor2.capture());

        Long arg1 = captor1.getValue();
        NewItemRequestRequest arg2 = captor2.getValue();

        assertEquals(userId, arg1);
        assertEquals("description1", arg2.getDescription());
    }

    @Test
    void create_whenInvokedWOHeader_thenResponseStatusBadRequest() throws Exception {
        String json = "{" +
                "\"description\":\"description1\"" +
                "}";

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        verify(itemRequestClient, never()).create(any(), any());
    }

    @Test
    void create_whenInvokedWithNullDescription_thenResponseStatusBadRequest() throws Exception {
        Long userId = 1L;
        String json = "{" +
                "}";

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        verify(itemRequestClient, never()).create(any(), any());
    }

    @Test
    void create_whenInvokedWithEmptyDescription_thenResponseStatusBadRequest() throws Exception {
        Long userId = 1L;
        String json = "{" +
                "\"description\":\"\"" +
                "}";

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        verify(itemRequestClient, never()).create(any(), any());
    }

    @Test
    void create_whenInvokedWithBlankDescription_thenResponseStatusBadRequest() throws Exception {
        Long userId = 1L;
        String json = "{" +
                "\"description\":\"           \"" +
                "}";

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        verify(itemRequestClient, never()).create(any(), any());
    }

    @Test
    void getAllByRequestorId_whenInvoked_thenResponseStatusOk() throws Exception {
        Long userId = 1L;

        when(itemRequestClient.getAllByRequestorId(any())).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        ArgumentCaptor<Long> captor1 = ArgumentCaptor.forClass(Long.class);
        verify(itemRequestClient).getAllByRequestorId(captor1.capture());

        Long arg1 = captor1.getValue();

        assertEquals(userId, arg1);
    }

    @Test
    void getAllByRequestorId_whenInvokedWOHeader_thenResponseStatusBadRequest() throws Exception {
        mockMvc.perform(get("/requests")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(itemRequestClient, never()).getAllByRequestorId(any());
    }

    @Test
    void getAll_whenInvoked_thenResponseStatusOk() throws Exception {
        Long userId = 1L;

        when(itemRequestClient.getAll(any())).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        ArgumentCaptor<Long> captor1 = ArgumentCaptor.forClass(Long.class);
        verify(itemRequestClient).getAll(captor1.capture());

        Long arg1 = captor1.getValue();

        assertEquals(userId, arg1);
    }

    @Test
    void getAll_whenInvokedWOHeader_thenResponseStatusBadRequest() throws Exception {
        mockMvc.perform(get("/requests/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(itemRequestClient, never()).getAll(any());
    }

    @Test
    void getByRequestId_whenInvoked_thenResponseStatusOk() throws Exception {
        Long userId = 1L;
        Long requestId = 1L;

        when(itemRequestClient.getByRequestId(any(), any())).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        ArgumentCaptor<Long> captor1 = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Long> captor2 = ArgumentCaptor.forClass(Long.class);
        verify(itemRequestClient).getByRequestId(captor1.capture(), captor2.capture());

        Long arg1 = captor1.getValue();
        Long arg2 = captor1.getValue();

        assertEquals(userId, arg1);
        assertEquals(requestId, arg2);
    }

    @Test
    void getByRequestId_whenInvokedWoHeader_thenResponseStatusOk() throws Exception {
        Long requestId = 1L;

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(itemRequestClient, never()).getByRequestId(any(), any());
    }
}