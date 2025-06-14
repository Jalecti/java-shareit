package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.NewItemRequestRequest;


@Service
public class ItemRequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    @Autowired
    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> create(Long userId, NewItemRequestRequest request) {
        return post("", userId, request);
    }

    public ResponseEntity<Object> getAllByRequestorId(Long requestorId) {
        return get("", requestorId);
    }

    public ResponseEntity<Object> getAll(Long requestorId) {
        return get("/all", requestorId);
    }

    public ResponseEntity<Object> getByRequestId(Long requestorId, Long requestId) {
        return get("/" + requestId, requestorId);
    }
}
