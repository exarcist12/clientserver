package ru.otus.java.application;

import ru.otus.java.application.dtos.Item;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ItemsRepository {
    private List<Item> items;

//    public ItemsRepository() {
//        this.items = new ArrayList<>(Arrays.asList(
//                new Item(1L, "Bread", BigDecimal.valueOf(35), new int[]{1}),
//                new Item(2L, "Milk", BigDecimal.valueOf(80), new int[]{1, 5}),
//                new Item(3L, "Cheese", BigDecimal.valueOf(400), new int[]{1})
//        ));
//    }

//    public ItemsRepository() {
//        this.items = new ArrayList<>(Arrays.asList(
//                new Item(1, "Bread", Integer.valueOf(35), List.of("books", "electronics", "sale")),
//                new Item(2, "Milk", Integer.valueOf(80), List.of("sale")),
//                new Item(3, "Cheese", Integer.valueOf(400), List.of("sale"))
//        ));
//    }

    public List<Item> getAll() {
        return Collections.unmodifiableList(items);
    }

    public Item getById(Integer id) {
        return items.stream().filter(i -> i.getId().equals(id)).findFirst().get();
    }

    public Item createNew(Item item) {
        Integer newId = items.stream().mapToInt(Item::getId).max().orElse(0) + 1;
        item.setId(newId);
        items.add(item);
        return item;
    }
}