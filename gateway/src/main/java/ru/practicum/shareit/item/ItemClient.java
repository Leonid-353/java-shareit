package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.dto.comment.NewCommentRequest;

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

    public ResponseEntity<Object> createItem(Long ownerId, NewItemRequest newItemRequest) {
        return post("", ownerId, newItemRequest);
    }

    public ResponseEntity<Object> getAllOwnerItems(Long ownerId) {
        return get("", ownerId);
    }

    public ResponseEntity<Object> getItem(Long itemId, Long userId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> updateItem(UpdateItemRequest updateItemRequest, Long itemId, Long ownerId) {
        return patch("/" + itemId, ownerId, updateItemRequest);
    }

    public ResponseEntity<Object> removeItem(Long itemId, Long ownerId) {
        return delete("/" + itemId, ownerId);
    }

    public ResponseEntity<Object> searchItemByNameOrDescription(String text, Long userId) {
        Map<String, Object> parameters = Map.of(
                "text", text
        );
        return get("/search?text={text}", userId, parameters);
    }

    public ResponseEntity<Object> createComment(NewCommentRequest newCommentRequest, Long itemId, Long bookerId) {
        return post("/" + itemId + "/comment", bookerId, newCommentRequest);
    }
}
