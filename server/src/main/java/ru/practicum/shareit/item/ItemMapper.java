package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.BookingShortDto;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;

import java.util.Collection;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ItemMapper {
    public static ItemDto mapToItemDto(Item item, Collection<CommentDto> comments) {
        return mapToItemDto(item, comments, null, null);
    }


    public static ItemDto mapToItemDto(Item item,
                                       Collection<CommentDto> comments,
                                       BookingShortDto prev, BookingShortDto next) {
        ItemDto itemDto = new ItemDto();

        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setOwner(UserMapper.mapToUserDto(item.getOwner()));
        if (item.getRequest() != null) {
            itemDto.setRequest(item.getRequest());
        }
        itemDto.setLastBooking(prev);
        itemDto.setNextBooking(next);
        itemDto.setComments(comments);
        return itemDto;
    }

    public static ItemShortDto mapToShortDto(Item item) {
        ItemShortDto itemShortDto = new ItemShortDto();

        itemShortDto.setId(item.getId());
        itemShortDto.setName(item.getName());
        itemShortDto.setAvailable(item.getAvailable());
        itemShortDto.setOwnerId(item.getOwner().getId());
        itemShortDto.setOwnerEmail(item.getOwner().getEmail());

        return itemShortDto;
    }

    public static Item mapToItem(NewItemRequest request, User owner, ItemRequest itemRequest) {
        Item item = new Item();

        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setAvailable(request.getAvailable());
        item.setOwner(owner);
        item.setRequest(itemRequest);

        return item;
    }

    public static Item updateItemFields(Item item, UpdateItemRequest request) {
        if (request.hasName()) {
            item.setName(request.getName());
        }
        if (request.hasDescription()) {
            item.setDescription(request.getDescription());
        }
        if (request.hasAvailable()) {
            item.setAvailable(request.getAvailable());
        }

        return item;
    }
}
