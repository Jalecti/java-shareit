package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.NewCommentRequest;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @Valid @RequestBody NewItemRequest newItemRequest) {
        log.info("Creating item: {} by userId={} ", newItemRequest, userId);
        return itemClient.create(userId, newItemRequest);
    }

    @GetMapping
    public ResponseEntity<Object> findAllByOwnerId(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Get items by ownerId={} ", ownerId);
        return itemClient.findAllByOwnerId(ownerId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> findAllByText(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @RequestParam("text") String text) {
        log.info("Get items by userId={} by text={} ", userId, text);
        return itemClient.findAllByText(userId, text);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findItemById(@PathVariable Long itemId) {
        log.info("Get item by itemId={}", itemId);
        return itemClient.findItemById(itemId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @PathVariable("itemId") Long itemId,
                                         @Valid @RequestBody UpdateItemRequest updateItemRequest) {
        log.info("Update item with id={} by userId={} body:{}", itemId, userId, updateItemRequest);
        return itemClient.update(userId, itemId, updateItemRequest);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> delete(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @PathVariable Long itemId) {
        log.info("Delete item with id={} by userId={}", itemId, userId);
        return itemClient.delete(userId, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> comment(@RequestHeader("X-Sharer-User-Id") Long authorId,
                                          @PathVariable Long itemId,
                                          @Valid @RequestBody NewCommentRequest request) {
        log.info("Create comment by authorId={} for itemId={} with body:{}", authorId, itemId, request);
        return itemClient.comment(authorId, itemId, request);
    }
}
