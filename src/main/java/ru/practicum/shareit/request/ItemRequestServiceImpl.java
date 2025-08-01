package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestCreatedDto;
import ru.practicum.shareit.request.dto.ItemRequestGetDto;
import ru.practicum.shareit.request.dto.ItemRequestInputDto;
import ru.practicum.shareit.request.dto.RequestItemInfoDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService{
    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequestCreatedDto createRequest(ItemRequestInputDto dto, long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            log.warn("Пользователь с id={} не найден.", userId);
            throw new NotFoundException("Пользователь с id=" + userId + " не найден.");
        }
        ItemRequest itemRequest = ItemRequestMapper.mapToRequestFromDto(dto);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setRequestor(optionalUser.get());
        return ItemRequestMapper.mapToDto(requestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestGetDto> getUserRequests(long userId) {
        List<ItemRequest> userRequests = requestRepository.findAllByRequestorIdOrderByCreatedDesc(userId);
        return mapToGetDtoAndGroupRequests(userRequests);
    }

    @Override
    public List<ItemRequestGetDto> getAllRequests() {
        List<ItemRequest> requests = requestRepository.findAllByOrderByCreatedDesc();
        return mapToGetDtoAndGroupRequests(requests);
    }

    @Override
    public ItemRequestGetDto getRequestById(long id) {
        ItemRequest request = requestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Нет запроса с id=" + id));
        List<Item> item = itemRepository.findAllByRequestId(id);
        return ItemRequestMapper.mapToGetDto(request, item);
    }

    private List<ItemRequestGetDto> mapToGetDtoAndGroupRequests(List<ItemRequest> requests) {
        List<Long> requestIds = requests.stream().map(ItemRequest::getId).toList();
        List<Item> requestItems = itemRepository.findAllByRequestIdInWithOwners(requestIds);
        Map<Long, List<RequestItemInfoDto>> groupedItemsByRequest = requestItems.stream()
                .map(item -> new RequestItemInfoDto(item.getId(), item.getName(), item.getOwner().getId()))
                .collect(Collectors.groupingBy(RequestItemInfoDto::getItemId));
        return requests.stream()
                .map(itemRequest -> new ItemRequestGetDto(
                        itemRequest.getId(),
                        itemRequest.getDescription(),
                        itemRequest.getCreated(),
                        groupedItemsByRequest.getOrDefault(itemRequest.getId(), List.of())))
                .toList();
    }
}