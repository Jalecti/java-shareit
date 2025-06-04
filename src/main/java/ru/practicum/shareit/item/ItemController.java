package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.NewCommentRequest;

import java.util.Collection;


@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private final ItemUtilService itemUtilService;

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @Valid @RequestBody NewItemRequest newItemRequest) {
        return itemUtilService.save(userId, newItemRequest);
    }

    @GetMapping
    public Collection<ItemDto> findAllByOwnerId(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return itemUtilService.findAllByOwnerId(ownerId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> findAllByText(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestParam("text") String text) {
        return itemService.findAllByText(text);
    }

    @GetMapping("/{itemId}")
    public ItemDto findItemById(@PathVariable Long itemId) {
        return itemService.findItemById(itemId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @PathVariable("itemId") Long itemId,
                          @Valid @RequestBody UpdateItemRequest updateItemRequest) {
        return itemService.update(userId, itemId, updateItemRequest);
    }

    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@RequestHeader("X-Sharer-User-Id") Long userId,
                       @PathVariable Long itemId) {
        itemService.delete(userId, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto comment(@RequestHeader("X-Sharer-User-Id") Long authorId,
                              @PathVariable Long itemId,
                              @Valid @RequestBody NewCommentRequest request) {
        return itemUtilService.comment(authorId, itemId, request);
    }
}
