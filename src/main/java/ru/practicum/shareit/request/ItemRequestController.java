package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;


@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;
    private final ItemRequestUtilService itemRequestUtilService;

    @PostMapping
    public ItemRequestDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @Valid @RequestBody NewItemRequestRequest request) {
        return itemRequestService.save(userId, request);
    }

    @GetMapping
    public Collection<ItemRequestDto> getAllByRequestorId(@RequestHeader("X-Sharer-User-Id") Long requestorId) {
        return itemRequestUtilService.findAllByRequestorId(requestorId, true);
    }

    @GetMapping("/all")
    public Collection<ItemRequestDto> getAll(@RequestHeader("X-Sharer-User-Id") Long requestorId) {
        return itemRequestUtilService.findAllByRequestorId(requestorId, false);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getByRequestId(@RequestHeader("X-Sharer-User-Id") Long requestorId,
                                         @PathVariable Long requestId) {
        return itemRequestUtilService.findItemRequestById(requestorId, requestId);
    }
}
