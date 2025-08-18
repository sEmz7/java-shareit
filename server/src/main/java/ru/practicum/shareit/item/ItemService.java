package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithDatesAndComments;

import java.util.List;

interface ItemService {
    ItemDto create(ItemDto itemDto, long userId);

    ItemDto update(ItemDto itemDto, long userId, long itemId);

    ItemDtoWithDatesAndComments getById(long itemId);

    List<ItemDtoWithDatesAndComments> getAllUserItems(long userId);

    List<ItemDto> getAllItemsByName(String text);

    CommentDto addCommentToItem(CommentDto commentDto, long itemId, long userId);
}
