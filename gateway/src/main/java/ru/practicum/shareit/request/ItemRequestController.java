package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.NewItemRequestRequest;


@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @Valid @RequestBody NewItemRequestRequest request) {
        log.info("Create item request by userId={}, body:{}", userId, request);
        return itemRequestClient.create(userId, request);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByRequestorId(@RequestHeader("X-Sharer-User-Id") Long requestorId) {
        log.info("Get all item requests by requestorId={}", requestorId);
        return itemRequestClient.getAllByRequestorId(requestorId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll(@RequestHeader("X-Sharer-User-Id") Long requestorId) {
        log.info("Get all other item requests by requestorId={}", requestorId);
        return itemRequestClient.getAll(requestorId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getByRequestId(@RequestHeader("X-Sharer-User-Id") Long requestorId,
                                         @PathVariable Long requestId) {
        log.info("Get item request by id={} by requestorId={}", requestId, requestorId);
        return itemRequestClient.getByRequestId(requestorId, requestId);
    }
}
