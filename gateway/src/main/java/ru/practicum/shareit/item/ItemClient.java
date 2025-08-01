package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> create(ItemDto itemDto, long userId) {
        return post("", userId, itemDto);
    }

    public ResponseEntity<Object> update(ItemDto itemDto, long userId, long itemId) {
        return post("/" + itemId, userId, itemId);
    }

    public ResponseEntity<Object> getById(long itemId) {
        return get("/" + itemId);
    }

    public ResponseEntity<Object> getAllUserItems(long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> getItemsByName(String text) {
        return get("/search?text=" + text);
    }

    public ResponseEntity<Object> addComment(CommentDto commentDto, long itemId, long userId) {
        return post("/" + itemId + "/comment", userId, commentDto);
    }
}
