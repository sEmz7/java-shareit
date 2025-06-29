package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto create(ItemDto itemDto, long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->  {
                log.warn("Нет пользователя с id={}", userId);
                return new NotFoundException("Нет пользователя с id=" + userId);
            }
        );
        Item item = ItemMapper.toItem(itemDto);
        item.setId(getNextId());
        item.setOwner(user);
        return ItemMapper.toItemDto(itemRepository.save(item, userId));
    }

    private long getNextId() {
        List<Item> items = itemRepository.findAll();
        if (items.isEmpty()) {
            return 1L;
        }
        return items
                .stream()
                .map(Item::getId)
                .max(Long::compareTo)
                .get() + 1L;
    }
}
