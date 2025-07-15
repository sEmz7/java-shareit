package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingsDates;
import ru.practicum.shareit.item.dto.CommentDto;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<ItemDto> createItem(@Valid @RequestBody ItemDto itemDto,
                                              @RequestHeader(USER_ID_HEADER) Long userId) {
        ItemDto createdItem = itemService.create(itemDto, userId);
        return ResponseEntity.ok(createdItem);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(@RequestBody ItemDto itemDto,
                                              @RequestHeader(USER_ID_HEADER) Long userId,
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
    public ResponseEntity<List<ItemDtoWithBookingsDates>> getAllUserItems(@RequestHeader(USER_ID_HEADER) Long userId) {
        List<ItemDtoWithBookingsDates> items = itemService.getAllUserItems(userId);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> getItemsByName(@RequestParam String text) {
        List<ItemDto> items = itemService.getAllItemsByName(text);
        return ResponseEntity.ok(items);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> addCommentToItem(@Valid @RequestBody CommentDto commentDto,
                                                       @PathVariable long itemId,
                                                       @RequestHeader(USER_ID_HEADER) Long userId) {
        CommentDto addedComment = itemService.addCommentToItem(commentDto, itemId, userId);
        return ResponseEntity.ok().body(addedComment);
    }
}
