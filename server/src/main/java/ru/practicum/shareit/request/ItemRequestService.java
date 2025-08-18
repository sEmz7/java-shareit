package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestCreatedDto;
import ru.practicum.shareit.request.dto.ItemRequestGetDto;
import ru.practicum.shareit.request.dto.ItemRequestInputDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestCreatedDto createRequest(ItemRequestInputDto dto, long userId);

    List<ItemRequestGetDto> getUserRequests(long userId);

    List<ItemRequestGetDto> getAllRequests();

    ItemRequestGetDto getRequestById(long id);
}
