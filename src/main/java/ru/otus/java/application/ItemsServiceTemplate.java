package ru.otus.java.application;

import ru.otus.java.application.dtos.Item;
import ru.otus.java.application.dtos.ItemMapper;

import java.sql.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ItemsServiceTemplate {

    private static final String DATABASE_URL = "jdbc:postgresql://localhost:5432/otus-db";
    private static final String DATABASE_USER = "admin";
    private static final String DATABASE_PASSWORD = "password";
    private static final String ITEMS_CATEGORIES = "SELECT \n" +
            "    i.id AS item_id,\n" +
            "    i.title,\n" +
            "    i.price,\n" +
            "    c.id AS category_id,\n" +
            "    c.category AS category_name\n" +
            "FROM item i\n" +
            "LEFT JOIN item_category ic ON i.id = ic.item_id\n" +
            "LEFT JOIN category c ON ic.category_id = c.id\n" +
            "ORDER BY i.id;";

    private static final String GET_ITEM = "SELECT  i.id AS item_id, i.title, i.price,  \n" +
            "        c.id AS category_id, c.category AS category_name\n" +
            "FROM item i\n" +
            "LEFT JOIN item_category ic ON i.id = ic.item_id\n" +
            "LEFT JOIN category c ON ic.category_id = c.id\n" +
            "WHERE i.id = ?";

    private static final String ADD_ITEM = "INSERT INTO item (title, price) VALUES (?, ?);";

    private static final String ADD_ITEM_WITH_ID = "INSERT INTO item (id, title, price) VALUES (?, ?, ?);";

    private static final String ADD_CATEGORY = "INSERT INTO category (category) VALUES (?);";
    private static final String ADD_ITEM_CATEGORY = "INSERT INTO item_category (item_id, category_id) VALUES (?, ?);";

    private static final String GET_ALL_CATEGORY = "select category from category";
    private static final String GET_CATEGORY = "select id from category where category =?";

    private static final String DELETE_ITEM_CATEGORY = "delete from item_category where item_id = ?";

    private static final String DELETE_ITEM = "delete from item where id = ?";

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
            try (ResultSet rs = statement.executeQuery(ITEMS_CATEGORIES)) {
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


    public List<String> getCategories(){
        List<String> result = new CopyOnWriteArrayList<>();
        try (Statement statement = connection.createStatement()) {
            try (ResultSet rs = statement.executeQuery(GET_ALL_CATEGORY)) {
                while (rs.next()) {
                    String category = rs.getString("category");
                    result.add(category);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }


    public Item getById(int id){
        Item result = null;
        try (PreparedStatement ps = connection.prepareStatement(GET_ITEM)) {
            ps.setInt(1, id);
            try (ResultSet resultSet = ps.executeQuery()) {
                while (resultSet.next()) {
                    result = ItemMapper.getItem(resultSet);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }


    public Item addNewItem(Item item){
        try (PreparedStatement ps = connection.prepareStatement(ADD_ITEM)) {
            ps.setString(1, item.getTitle());
            ps.setInt(2, item.getPrice());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        for (String category : item.getCategories()){
            if(!getCategories().contains(category)) {
                try (PreparedStatement ps = connection.prepareStatement(ADD_CATEGORY)) {
                    ps.setString(1, category);
                    ps.executeUpdate();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        List<Item> items = getAllItems();
        int id = getAllItems().stream().filter(p1->p1.getTitle().equals(item.getTitle())).map(p1->p1.getId()).findFirst().get();
        for (String category : item.getCategories()){
            int idCategory = 0;
            try (PreparedStatement ps = connection.prepareStatement(GET_CATEGORY)) {
                ps.setString(1, category);
                try (ResultSet resultSet = ps.executeQuery()) {
                    while (resultSet.next()) {
                        idCategory = Integer.valueOf(resultSet.getString("id"));;
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }


            try (PreparedStatement ps = connection.prepareStatement(ADD_ITEM_CATEGORY)) {
                    ps.setInt(1, id);
                    ps.setInt(2, idCategory);
                    ps.executeUpdate();
            } catch (SQLException e) {
                    throw new RuntimeException(e);
            }

        }

        items = getAllItems();
        return getAllItems().stream().filter(p1->p1.getTitle().equals(item.getTitle())).findFirst().get();

    }

    public Item updateItem(Item item){
        try (PreparedStatement ps = connection.prepareStatement(DELETE_ITEM_CATEGORY)) {
            ps.setInt(1, item.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try (PreparedStatement ps = connection.prepareStatement(DELETE_ITEM)) {
            ps.setInt(1, item.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try (PreparedStatement ps = connection.prepareStatement(ADD_ITEM_WITH_ID)) {
            ps.setInt(1, item.getId());
            ps.setString(2, item.getTitle());
            ps.setInt(3, item.getPrice());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        for (String category : item.getCategories()){
            if(!getCategories().contains(category)) {
                try (PreparedStatement ps = connection.prepareStatement(ADD_CATEGORY)) {
                    ps.setString(1, category);
                    ps.executeUpdate();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        for (String category : item.getCategories()){
            int idCategory = 0;
            try (PreparedStatement ps = connection.prepareStatement(GET_CATEGORY)) {
                ps.setString(1, category);
                try (ResultSet resultSet = ps.executeQuery()) {
                    while (resultSet.next()) {
                        idCategory = Integer.valueOf(resultSet.getString("id"));;
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }


            try (PreparedStatement ps = connection.prepareStatement(ADD_ITEM_CATEGORY)) {
                ps.setInt(1, item.getId());
                ps.setInt(2, idCategory);
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }

        return getById(item.getId());
    }
}
