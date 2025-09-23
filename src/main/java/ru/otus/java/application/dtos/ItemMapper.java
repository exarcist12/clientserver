package ru.otus.java.application.dtos;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ItemMapper {

    private static Item lastItem;

    public static  Item getItem(ResultSet rs) throws SQLException {
        Item item = null;
        String id = rs.getString("item_id");
        if (lastItem == null || !id.equals(String.valueOf(lastItem.getId()))) {
            String title = rs.getString("title");
            String price = rs.getString("price");
            String categories = rs.getString("category_name");
            List<String> catList =
                    categories == null ? new ArrayList<>() : new ArrayList<>(List.of(categories));
            item = new Item(Integer.valueOf(id), title, Integer.valueOf(price), catList);
        } else if(id.equals(String.valueOf(lastItem.getId()))) {
            item = lastItem;
            String category = rs.getString("category_name");
            List<String> categories = item.getCategories();
            categories.add(category);
            item.setCategories(categories);
        }
        lastItem = item;

        return item;
    }
}
