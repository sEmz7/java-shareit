package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.request.dto.ItemRequestGetDto;
import ru.practicum.shareit.request.dto.RequestItemInfoDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestGetDtoJsonTest {

    @Autowired
    private JacksonTester<ItemRequestGetDto> json;

    @Test
    void testSerializeDeserialize() throws Exception {
        RequestItemInfoDto requestItemInfoDto = new RequestItemInfoDto(10L, "itemName", 5L);

        ItemRequestGetDto dto = new ItemRequestGetDto(
                1L,
                "description text",
                LocalDateTime.of(2025, 8, 4, 12, 0),
                List.of(requestItemInfoDto)
        );

        var jsonContent = json.write(dto);

        assertThat(jsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathStringValue("$.description").isEqualTo("description text");
        assertThat(jsonContent).extractingJsonPathStringValue("$.created").isEqualTo("2025-08-04T12:00:00");

        assertThat(jsonContent).extractingJsonPathArrayValue("$.items").hasSize(1);
        assertThat(jsonContent).extractingJsonPathNumberValue("$.items[0].itemId").isEqualTo(10);
        assertThat(jsonContent).extractingJsonPathStringValue("$.items[0].name").isEqualTo("itemName");
        assertThat(jsonContent).extractingJsonPathNumberValue("$.items[0].ownerId").isEqualTo(5);

        ItemRequestGetDto parsed = json.parseObject(jsonContent.getJson());
        assertThat(parsed.getId()).isEqualTo(dto.getId());
        assertThat(parsed.getDescription()).isEqualTo(dto.getDescription());
        assertThat(parsed.getCreated()).isEqualTo(dto.getCreated());
        assertThat(parsed.getItems()).hasSize(1);
        assertThat(parsed.getItems().get(0).getItemId()).isEqualTo(10L);
    }
}