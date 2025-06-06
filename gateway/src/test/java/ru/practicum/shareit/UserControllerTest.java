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
import ru.practicum.shareit.user.UserClient;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertEquals;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserClient userClient;

    @Test
    void create_whenInvokedWithCorrectJson_thenResponseStatusOk() throws Exception {
        String json = "{" +
                "\"name\":\"name1\"," +
                "\"email\":\"email1@email.com\"" +
                "}";

        when(userClient.create(any())).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        ArgumentCaptor<NewUserRequest> captor = ArgumentCaptor.forClass(NewUserRequest.class);
        verify(userClient).create(captor.capture());

        NewUserRequest request = captor.getValue();

        assertEquals("name1", request.getName());
        assertEquals("email1@email.com", request.getEmail());
    }

    @Test
    void create_whenInvokedWithEmptyName_thenResponseStatusBadRequest() throws Exception {
        String json = "{" +
                "\"name\":\"\"," +
                "\"email\":\"email1@email.com\"" +
                "}";

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).create(any());
    }

    @Test
    void create_whenInvokedWithBlankName_thenResponseStatusBadRequest() throws Exception {
        String json = "{" +
                "\"name\":\"           \"," +
                "\"email\":\"email1@email.com\"" +
                "}";

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).create(any());
    }

    @Test
    void create_whenInvokedWithNullName_thenResponseStatusBadRequest() throws Exception {
        String json = "{" +
                "\"email\":\"email1@email.com\"" +
                "}";

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).create(any());
    }

    @Test
    void create_whenInvokedWithIncorrectEmail_thenResponseStatusBadRequest() throws Exception {
        String json = "{" +
                "\"name\":\"name1\"," +
                "\"email\":\"email1email.com\"" +
                "}";

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).create(any());
    }

    @Test
    void create_whenInvokedWithEmptyEmail_thenResponseStatusBadRequest() throws Exception {
        String json = "{" +
                "\"name\":\"name1\"," +
                "\"email\":\"\"" +
                "}";

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).create(any());
    }

    @Test
    void create_whenInvokedWithBlankEmail_thenResponseStatusBadRequest() throws Exception {
        String json = "{" +
                "\"name\":\"name1\"," +
                "\"email\":\"                  \"" +
                "}";

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).create(any());
    }

    @Test
    void create_whenInvokedWithNullEmail_thenResponseStatusBadRequest() throws Exception {
        String json = "{" +
                "\"name\":\"name1\"" +
                "}";

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).create(any());
    }

    @Test
    void findAll_whenInvoked_thenResponseStatusOk() throws Exception {
        when(userClient.findAll()).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());

        verify(userClient).findAll();
    }

    @Test
    void findUserById_whenInvokedWithCorrectId_thenResponseStatusOk() throws Exception {
        Long userId = 1L;

        when(userClient.findUserById(userId)).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk());

        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
        verify(userClient).findUserById(captor.capture());

        Long value = captor.getValue();

        assertEquals(userId, value);
    }

    @Test
    void findUserById_whenInvokedWithNotFoundId_thenResponseStatusNotFound() throws Exception {
        Long userId = 1L;

        when(userClient.findUserById(userId)).thenReturn(ResponseEntity.notFound().build());

        mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isNotFound());

        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
        verify(userClient).findUserById(captor.capture());

        Long value = captor.getValue();

        assertEquals(userId, value);
    }

    @Test
    void update_whenInvokedWithCorrectJson_thenResponseStatusOk() throws Exception {
        Long userId = 1L;
        String json = "{" +
                "\"name\":\"name1\"," +
                "\"email\":\"email1@email.com\"" +
                "}";

        when(userClient.update(any(), any())).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        ArgumentCaptor<Long> captor1 = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<UpdateUserRequest> captor2 = ArgumentCaptor.forClass(UpdateUserRequest.class);
        verify(userClient).update(captor1.capture(), captor2.capture());

        Long arg1 = captor1.getValue();
        UpdateUserRequest arg2 = captor2.getValue();

        assertEquals(userId, arg1);
        assertEquals("name1", arg2.getName());
        assertEquals("email1@email.com", arg2.getEmail());
    }

    @Test
    void update_whenInvokedWithInCorrectEmail_thenResponseStatusBadRequest() throws Exception {
        Long userId = 1L;
        String json = "{" +
                "\"name\":\"name1\"," +
                "\"email\":\"email1email.com\"" +
                "}";

        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).update(any(), any());
    }

    @Test
    void delete_whenInvokedWithCorrectId_thenResponseStatusNoContent() throws Exception {
        Long userId = 1L;

        when(userClient.delete(userId)).thenReturn(ResponseEntity.noContent().build());

        mockMvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isNoContent());

        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
        verify(userClient).delete(captor.capture());

        Long value = captor.getValue();

        assertEquals(userId, value);
    }

    @Test
    void delete_whenInvokedWithNotFoundId_thenResponseStatusNotFound() throws Exception {
        Long userId = 1L;

        when(userClient.delete(userId)).thenReturn(ResponseEntity.notFound().build());

        mockMvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isNotFound());

        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
        verify(userClient).delete(captor.capture());

        Long value = captor.getValue();

        assertEquals(userId, value);
    }
}