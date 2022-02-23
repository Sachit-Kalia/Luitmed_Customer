package com.luitmed.prashantimedicos;

public class CartItem {
    String id;
    String pId;
    String name;
    String price;
    String cost;
    String quantity;
    String number;
    String icon;

    public CartItem(){

    }

    public CartItem(String id, String pId, String name, String price, String cost, String quantity, String number, String icon) {
        this.id = id;
        this.pId = pId;
        this.name = name;
        this.price = price;
        this.cost = cost;
        this.quantity = quantity;
        this.number = number;
        this.icon = icon;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}

