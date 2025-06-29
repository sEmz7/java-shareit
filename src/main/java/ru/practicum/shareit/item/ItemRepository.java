package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

interface ItemRepository {
    List<Item> findAll();

    Item save(Item item, long userId);
}
