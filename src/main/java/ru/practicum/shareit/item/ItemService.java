package ru.practicum.shareit.item;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithDatesAndComments;
import ru.practicum.shareit.item.dto.CommentDto;

import java.util.List;

@Transactional(readOnly = true)
interface ItemService {
    @Transactional
    ItemDto create(ItemDto itemDto, long userId);

    @Transactional
    ItemDto update(ItemDto itemDto, long userId, long itemId);

    ItemDtoWithDatesAndComments getById(long itemId);

    List<ItemDtoWithDatesAndComments> getAllUserItems(long userId);

    List<ItemDto> getAllItemsByName(String text);

    @Transactional
    CommentDto addCommentToItem(CommentDto commentDto, long itemId, long userId);
}
