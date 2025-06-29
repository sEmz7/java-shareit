package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

interface ItemRepository {
    List<Item> findAll();

    Item save(Item item);

    Item update(Item item);

    Optional<Item> findById(long itemId);
}
