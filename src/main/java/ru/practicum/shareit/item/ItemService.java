package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

interface ItemService {
    ItemDto create(ItemDto itemDto, long userId);
}
