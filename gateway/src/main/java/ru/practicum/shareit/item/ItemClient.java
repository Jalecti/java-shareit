package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.NewCommentRequest;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

import java.util.ArrayList;
import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> create(Long userId, NewItemRequest newItemRequest) {
        return post("", userId, newItemRequest);
    }

    public ResponseEntity<Object> findAllByOwnerId(Long ownerId) {
        return get("", ownerId);
    }

    public ResponseEntity<Object> findAllByText(Long userId, String text) {
        if (text.isBlank()) return ResponseEntity.ok(new ArrayList<>());
        Map<String, Object> parameters = Map.of(
                "text", text
        );
        return get("/search?text={text}", userId, parameters);
    }

    public ResponseEntity<Object> findItemById(Long itemId) {
        return get("/" + itemId);
    }

    public ResponseEntity<Object> update(Long userId, Long itemId, UpdateItemRequest updateItemRequest) {
        return patch("/" + itemId, userId, updateItemRequest);
    }

    public ResponseEntity<Object> delete(Long userId, Long itemId) {
        return delete("/" + itemId, userId);
    }

    public ResponseEntity<Object> comment(Long authorId, Long itemId, NewCommentRequest request) {
        return post("/" + itemId + "/comment", authorId, request);
    }

}
