package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestCreatedDto;
import ru.practicum.shareit.request.dto.ItemRequestGetDto;
import ru.practicum.shareit.request.dto.ItemRequestInputDto;
import ru.practicum.shareit.request.dto.RequestItemInfoDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.ArrayList;
import java.util.List;

public final class ItemRequestMapper {

    private ItemRequestMapper() {

    }

    public static ItemRequest mapToRequestFromDto(ItemRequestInputDto dto) {
        return new ItemRequest(null, dto.getDescription(), null, null);
    }

    public static ItemRequestCreatedDto mapToDto(ItemRequest request) {
        return new ItemRequestCreatedDto(request.getId(), request.getDescription(), request.getCreated());
    }

    public static ItemRequestGetDto mapToGetDto(ItemRequest request, List<Item> items) {
        return new ItemRequestGetDto(
                request.getId(),
                request.getDescription(),
                request.getCreated(),
                items.stream().map(item -> new RequestItemInfoDto(
                            item.getId(),
                            item.getName(),
                            item.getOwner().getId()))
                        .toList());
    }
}