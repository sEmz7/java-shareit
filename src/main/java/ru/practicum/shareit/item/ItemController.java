package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemDto> createItem(@Valid @RequestBody ItemDto itemDto,
                                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        ItemDto createdItem = itemService.create(itemDto, userId);
        return ResponseEntity.ok(createdItem);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(@RequestBody ItemDto itemDto,
                                              @RequestHeader("X-Sharer-User-Id") Long userId,
                                              @PathVariable Long itemId) {
        ItemDto updatedItem = itemService.update(itemDto, userId, itemId);
        return ResponseEntity.ok(updatedItem);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItemById(@PathVariable long itemId) {
        ItemDto itemDto = itemService.getById(itemId);
        return ResponseEntity.ok(itemDto);
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getAllUserItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        List<ItemDto> items = itemService.getAllUserItems(userId);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> getItemsByName(@RequestParam(value = "text") String text) {
        List<ItemDto> items = itemService.getAllItemsByName(text);
        return ResponseEntity.ok(items);
    }
}
