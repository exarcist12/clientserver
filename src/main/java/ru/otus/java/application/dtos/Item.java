package ru.otus.java.application.dtos;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Item {
    private Integer id;
    private String title;
    private Integer price;

    private List<String> categories = new ArrayList<>();

    public  List<String>  getCategories() {
        return categories;
    }

    public void setCategories( List<String>  categories) {
        this.categories = categories;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Item() {
    }

    public Item(Integer id, String title, Integer price, List<String> categories) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.categories = categories;
    }
}

