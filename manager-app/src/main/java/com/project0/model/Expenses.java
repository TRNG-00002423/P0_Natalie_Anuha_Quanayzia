package com.project0.model;

import java.math.BigDecimal;

public class Expenses {


    int id;
    int user_id;
    BigDecimal amount;
    String description;
    String date;
    String category;

    public Expenses(int id, int user_id, BigDecimal amount, String description, String date, String category) {
        this.id = id;
        this.user_id = user_id;
        this.amount = amount;
        this.description = description;
        this.date = date;
        this.category = category;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}