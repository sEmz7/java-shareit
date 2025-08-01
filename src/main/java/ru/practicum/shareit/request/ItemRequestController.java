package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreatedDto;
import ru.practicum.shareit.request.dto.ItemRequestGetDto;
import ru.practicum.shareit.request.dto.ItemRequestInputDto;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemRequestService requestService;

    @PostMapping
    public ResponseEntity<ItemRequestCreatedDto> createRequest(@Valid @RequestBody ItemRequestInputDto dto,
                                                               @RequestHeader(USER_ID_HEADER) long userId) {
        ItemRequestCreatedDto createdDto = requestService.createRequest(dto, userId);
        return ResponseEntity.ok(createdDto);
    }

    @GetMapping
    public ResponseEntity<List<ItemRequestGetDto>> getUserRequests(@RequestHeader(USER_ID_HEADER) long userId) {
        List<ItemRequestGetDto> requests = requestService.getUserRequests(userId);
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestGetDto>> getAllRequests() {
        List<ItemRequestGetDto> requests = requestService.getAllRequests();
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestGetDto> getRequestInfoById(@PathVariable long requestId) {
        ItemRequestGetDto request = requestService.getRequestById(requestId);
        return ResponseEntity.ok(request);
    }
}