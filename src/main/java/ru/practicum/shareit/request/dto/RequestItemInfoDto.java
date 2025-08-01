package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class RequestItemInfoDto {
    private Long itemId;

    private String itemName;

    private Long ownerId;
}
