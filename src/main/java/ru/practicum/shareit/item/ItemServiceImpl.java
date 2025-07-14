package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.InvalidUserInputException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto create(ItemDto itemDto, long userId) {
        User user = findUserOrThrow(userId);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(user);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto update(ItemDto itemDto, long userId, long itemId) {
        findUserOrThrow(userId);
        Item item = findItemOrThrow(itemId);
        if (item.getOwner().getId() != userId) {
            log.warn("Редактировать вещь может только ее владелец.");
            throw new InvalidUserInputException("Редактировать вещь может только ее владелец.");
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto getById(long itemId) {
        return ItemMapper.toItemDto(findItemOrThrow(itemId));
    }

    @Override
    public List<ItemDto> getAllUserItems(long userId) {
        findUserOrThrow(userId);
        return ItemMapper.mapListToDto(itemRepository.findAllByOwnerId(userId));
    }

    @Override
    public List<ItemDto> getAllItemsByName(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return ItemMapper.mapListToDto(itemRepository.searchAvailableItemsByNameOrDescription(text));
    }

    private User findUserOrThrow(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> {
            log.warn("Нет пользователя с id={}", userId);
            return new NotFoundException("Нет пользователя с id=" + userId);
        });
    }

    private Item findItemOrThrow(long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> {
            log.warn("Нет вещи с id={}", itemId);
            return new NotFoundException("Нет вещи с id=" + itemId);
        });
    }
}
