package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private static final Map<Long, Item> items = new HashMap<>();

    @Override
    public List<Item> findAll() {
        return items.values().stream().toList();
    }

    @Override
    public Item save(Item item, long userId) {
        items.put(item.getId(), item);
        return item;
    }
}
