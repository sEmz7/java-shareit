package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

interface ItemService {
    ItemDto create(ItemDto itemDto, long userId);

    ItemDto update(ItemDto itemDto, long userId, long itemId);

    ItemDto getById(long itemId);

    List<ItemDto> getAllUserItems(long userId);

    List<ItemDto> getAllItemsByName(String text);
}
