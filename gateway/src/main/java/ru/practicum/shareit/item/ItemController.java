package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;


@Controller
@RequiredArgsConstructor
@RequestMapping(
        path = "items",
        produces = "application/json"
)
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getItemsByOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemClient.getItemsByOwner(userId);
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody ItemDto itemDto) {
        return itemClient.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItemById(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @RequestBody ItemDto itemDto) {
        return itemClient.updateItemById(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable Long itemId) {
        return itemClient.getItemById(itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchForItems(@RequestParam String text) {
        return itemClient.searchForItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> postComment(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId,
                                              @RequestBody CommentCreateDto commentCreate) {
        return itemClient.postComment(itemId, userId, commentCreate);
    }
}
