package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {

    @InjectMocks
    private ItemServiceImpl itemService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    private User user = new User(
            1L,
            "John",
            "john.doe@mail.com"
    );

    private ItemDto itemDto = ItemDto.builder()
            .name("name item")
            .description("description item")
            .available(true)
            .build();

    private Item item = Item.builder()
            .id(1L)
            .name(itemDto.getName())
            .description(itemDto.getDescription())
            .available(itemDto.getAvailable())
            .itemRequest(null)
            .owner(user)
            .build();


    @Test
    void createItemTest_whenEmptyItemRequest() throws Exception {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        when(itemRepository.save(any(Item.class)))
                .thenAnswer(invocation -> {
                    Item saved = invocation.getArgument(0);
                    saved.setId(1L); // Имитируем генерацию ID базой
                    return saved;
                });

        Item savedItem = itemService.createItem(user.getId(), itemDto);
        assertThat(savedItem)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(item);

        Mockito.verify(itemRepository, times(1))
                .save(item);
    }

    @Test
    void updateItemTest_whenDataCorrect() throws Exception {
        Item item2 = Item.builder()
                .id(1L)
                .name("NEW NAME UPDATED")
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .itemRequest(null)
                .owner(user)
                .build();

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        when(itemRepository.save(any(Item.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0, Item.class));

        Item updatedItem = itemService.updateItem(user.getId(), item2.getId(), ItemMapper.mapToItemDto(item2));

        assertThat(updatedItem)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(item);

        Mockito.verify(userRepository, times(1))
                .findById(anyLong());

        Mockito.verify(itemRepository, times(1))
                .findById(anyLong());

        Mockito.verify(itemRepository, times(1))
                .save(item);
    }
}
