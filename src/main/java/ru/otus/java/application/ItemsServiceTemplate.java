package ru.otus.java.application;

import ru.otus.java.application.dtos.Item;
import ru.otus.java.application.dtos.ItemMapper;

import java.sql.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class ItemsServiceTemplate {

    private static final String DATABASE_URL = "jdbc:postgresql://localhost:5432/otus-db";
    private static final String DATABASE_USER = "admin";
    private static final String DATABASE_PASSWORD = "password";
    private final Connection connection;

    public ItemsServiceTemplate() {
        try{
            connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Item> getAllItems(){
        List<Item> result = new CopyOnWriteArrayList<>();
        try (Statement statement = connection.createStatement()) {
            try (ResultSet rs = statement.executeQuery("SELECT \n" +
                    "    i.id AS item_id,\n" +
                    "    i.title,\n" +
                    "    i.price,\n" +
                    "    c.id AS category_id,\n" +
                    "    c.category AS category_name\n" +
                    "FROM item i\n" +
                    "LEFT JOIN item_category ic ON i.id = ic.item_id\n" +
                    "LEFT JOIN category c ON ic.category_id = c.id\n" +
                    "ORDER BY i.id;")) {
                while (rs.next()) {
                    Item currentItem = ItemMapper.getItem(rs);
                    if (result.stream().map(p1 -> p1.getId())
                            .noneMatch(id -> id.equals(currentItem.getId()))) {
                        result.add(currentItem);
                    }

                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }


    public List<Item> getAllItems2(){
        List<Item> result = new CopyOnWriteArrayList<>();
        try (Statement statement = connection.createStatement()) {
            try (ResultSet rs = statement.executeQuery("select * from category")) {
                while (rs.next()) {
                    Item currentItem = ItemMapper.getItem(rs);
                    for(Item item : result){
                        if (item.getId().equals(currentItem.getId())){
                            result.remove(item);
                            result.add(currentItem);
                        }
                    }
                    result.add(currentItem);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}
