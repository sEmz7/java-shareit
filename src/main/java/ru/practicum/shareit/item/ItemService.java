package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingsDates;
import ru.practicum.shareit.item.dto.CommentDto;

import java.util.List;

interface ItemService {
    ItemDto create(ItemDto itemDto, long userId);

    ItemDto update(ItemDto itemDto, long userId, long itemId);

    ItemDto getById(long itemId);

    List<ItemDtoWithBookingsDates> getAllUserItems(long userId);

    List<ItemDto> getAllItemsByName(String text);

    CommentDto addCommentToItem(CommentDto commentDto, long itemId, long userId);
}
