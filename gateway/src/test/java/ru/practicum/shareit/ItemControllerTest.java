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
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.NewCommentRequest;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(controllers = ItemController.class)
@AutoConfigureMockMvc
class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemClient itemClient;

    @Test
    void create_whenInvokedWithCorrectJson_thenResponseStatusOk() throws Exception {
        Long userId = 1L;
        String json = "{" +
                "\"name\":\"name1\"," +
                "\"description\":\"description1\"," +
                "\"available\":false" +
                "}";

        when(itemClient.create(any(), any())).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        ArgumentCaptor<Long> captor1 = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<NewItemRequest> captor2 = ArgumentCaptor.forClass(NewItemRequest.class);
        verify(itemClient).create(captor1.capture(), captor2.capture());

        Long arg1 = captor1.getValue();
        NewItemRequest arg2 = captor2.getValue();

        assertEquals(userId, arg1);
        assertEquals("name1", arg2.getName());
        assertEquals("description1", arg2.getDescription());
        assertEquals(false, arg2.getAvailable());
    }


    @Test
    void create_whenInvokedWithEmptyName_thenResponseStatusBadRequest() throws Exception {
        Long userId = 1L;
        String json = "{" +
                "\"name\":\"\"," +
                "\"description\":\"description1\"," +
                "\"available\":false" +
                "}";

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).create(any(), any());
    }

    @Test
    void create_whenInvokedWithBlankName_thenResponseStatusBadRequest() throws Exception {
        Long userId = 1L;
        String json = "{" +
                "\"name\":\"            \"," +
                "\"description\":\"description1\"," +
                "\"available\":false" +
                "}";

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).create(any(), any());
    }

    @Test
    void create_whenInvokedWithNullName_thenResponseStatusBadRequest() throws Exception {
        Long userId = 1L;
        String json = "{" +
                "\"description\":\"description1\"," +
                "\"available\":false" +
                "}";

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).create(any(), any());
    }

    @Test
    void create_whenInvokedWithEmptyDescription_thenResponseStatusBadRequest() throws Exception {
        Long userId = 1L;
        String json = "{" +
                "\"name\":\"name1\"," +
                "\"description\":\"\"," +
                "\"available\":false" +
                "}";

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).create(any(), any());
    }

    @Test
    void create_whenInvokedWithBlankDescription_thenResponseStatusBadRequest() throws Exception {
        Long userId = 1L;
        String json = "{" +
                "\"name\":\"name1\"," +
                "\"description\":\"            \"," +
                "\"available\":false" +
                "}";

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).create(any(), any());
    }

    @Test
    void create_whenInvokedWithNullDescription_thenResponseStatusBadRequest() throws Exception {
        Long userId = 1L;
        String json = "{" +
                "\"name\":\"name1\"," +
                "\"available\":false" +
                "}";

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).create(any(), any());
    }

    @Test
    void create_whenInvokedWithNullAvailable_thenResponseStatusBadRequest() throws Exception {
        Long userId = 1L;
        String json = "{" +
                "\"name\":\"name1\"," +
                "\"description\":\"description1\"" +
                "}";

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).create(any(), any());
    }

    @Test
    void create_whenInvokedWOHeader_thenResponseStatusBadRequest() throws Exception {
        String json = "{" +
                "\"name\":\"name1\"," +
                "\"description\":\"description1\"," +
                "\"available\":false" +
                "}";

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).create(any(), any());
    }

    @Test
    void findAllByOwnerId_whenInvoked_thenResponseStatusOk() throws Exception {
        Long userId = 1L;
        when(itemClient.findAllByOwnerId(any())).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        ArgumentCaptor<Long> captor1 = ArgumentCaptor.forClass(Long.class);
        verify(itemClient).findAllByOwnerId(captor1.capture());

        Long arg1 = captor1.getValue();

        assertEquals(userId, arg1);
    }

    @Test
    void findAllByOwnerId_whenInvokedWOHeader_thenResponseStatusBadRequest() throws Exception {
        mockMvc.perform(get("/items")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).findAllByOwnerId(any());
    }

    @Test
    void findAllByText_whenInvoked_thenResponseStatusOk() throws Exception {
        Long userId = 1L;
        String text = "text";
        when(itemClient.findAllByText(any(), any())).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId)
                        .param("text", text)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        ArgumentCaptor<Long> captor1 = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<String> captor2 = ArgumentCaptor.forClass(String.class);
        verify(itemClient).findAllByText(captor1.capture(), captor2.capture());

        Long arg1 = captor1.getValue();
        String arg2 = captor2.getValue();

        assertEquals(userId, arg1);
        assertEquals(text, arg2);
    }

    @Test
    void findAllByText_whenInvokedWOHeader_thenResponseStatusBadRequest() throws Exception {
        String text = "text";
        mockMvc.perform(get("/items/search")
                        .param("text", text)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).findAllByText(any(), any());
    }

    @Test
    void findAllByText_whenInvokedWOParam_thenResponseStatusBadRequest() throws Exception {
        Long userId = 1L;
        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).findAllByText(any(), any());
    }

    @Test
    void findItemById_whenInvoked_thenResponseStatusOk() throws Exception {
        Long itemId = 1L;
        when(itemClient.findItemById(any())).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/items/{itemId}", itemId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        ArgumentCaptor<Long> captor1 = ArgumentCaptor.forClass(Long.class);
        verify(itemClient).findItemById(captor1.capture());

        Long arg1 = captor1.getValue();

        assertEquals(itemId, arg1);
    }


    @Test
    void update_whenInvokedWithCorrectJson_thenResponseStatusOk() throws Exception {
        Long userId = 1L;
        Long itemId = 1L;
        String json = "{" +
                "\"name\":\"name1\"," +
                "\"description\":\"description1\"," +
                "\"available\":false" +
                "}";

        when(itemClient.update(any(), any(), any())).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        ArgumentCaptor<Long> captor1 = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Long> captor2 = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<UpdateItemRequest> captor3 = ArgumentCaptor.forClass(UpdateItemRequest.class);
        verify(itemClient).update(captor1.capture(), captor2.capture(), captor3.capture());

        Long arg1 = captor1.getValue();
        Long arg2 = captor2.getValue();
        UpdateItemRequest arg3 = captor3.getValue();

        assertEquals(userId, arg1);
        assertEquals(itemId, arg2);
        assertEquals("name1", arg3.getName());
        assertEquals("description1", arg3.getDescription());
        assertEquals(false, arg3.getAvailable());
    }

    @Test
    void update_whenInvokedWOHeader_thenResponseStatusBadRequest() throws Exception {
        Long itemId = 1L;
        String json = "{" +
                "\"name\":\"name1\"," +
                "\"description\":\"description1\"," +
                "\"available\":false" +
                "}";

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).update(any(), any(), any());
    }

    @Test
    void delete_whenInvoked_thenResponseStatusNoContent() throws Exception {
        Long itemId = 1L;
        Long userId = 1L;
        when(itemClient.delete(any(), any())).thenReturn(ResponseEntity.noContent().build());

        mockMvc.perform(delete("/items/{itemId}", itemId)
                        .header(("X-Sharer-User-Id"), userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        ArgumentCaptor<Long> captor1 = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Long> captor2 = ArgumentCaptor.forClass(Long.class);
        verify(itemClient).delete(captor1.capture(), captor2.capture());

        Long arg1 = captor1.getValue();
        Long arg2 = captor2.getValue();

        assertEquals(userId, arg1);
        assertEquals(itemId, arg2);
    }

    @Test
    void delete_whenInvokedWOHeader_thenResponseStatusBadRequest() throws Exception {
        Long itemId = 1L;
        mockMvc.perform(delete("/items/{itemId}", itemId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).delete(any(), any());
    }

    @Test
    void comment_whenInvokedWithCorrectJson_thenResponseStatusOk() throws Exception {
        Long authorId = 1L;
        Long itemId = 1L;
        String json = "{" +
                "\"text\":\"text1\"" +
                "}";

        when(itemClient.comment(any(), any(), any())).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", authorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        ArgumentCaptor<Long> captor1 = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Long> captor2 = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<NewCommentRequest> captor3 = ArgumentCaptor.forClass(NewCommentRequest.class);
        verify(itemClient).comment(captor1.capture(), captor2.capture(), captor3.capture());

        Long arg1 = captor1.getValue();
        Long arg2 = captor1.getValue();
        NewCommentRequest arg3 = captor3.getValue();

        assertEquals(authorId, arg1);
        assertEquals(itemId, arg2);
        assertEquals("text1", arg3.getText());

    }

    @Test
    void comment_whenInvokedWOHeader_thenResponseStatusBadRequest() throws Exception {
        Long itemId = 1L;
        String json = "{" +
                "\"text\":\"text1\"" +
                "}";
        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).comment(any(), any(), any());
    }

    @Test
    void comment_whenInvokedWithEmptyText_thenResponseStatusBadRequest() throws Exception {
        Long authorId = 1L;
        Long itemId = 1L;
        String json = "{" +
                "\"text\":\"\"" +
                "}";
        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", authorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).comment(any(), any(), any());
    }

    @Test
    void comment_whenInvokedWithBlankText_thenResponseStatusBadRequest() throws Exception {
        Long authorId = 1L;
        Long itemId = 1L;
        String json = "{" +
                "\"text\":\"           \"" +
                "}";
        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", authorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).comment(any(), any(), any());
    }
}