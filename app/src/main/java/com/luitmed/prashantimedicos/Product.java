package com.luitmed.prashantimedicos;

public class Product {

    private String productID;
    private String title;
    private String description;
    private String category;
    private String quantity;
    private String productIcon;
    private String price;
    private String discountedPrice;
    private String timestamp;
    private String uid;
    private String stock;

    public Product(String productID, String title, String description, String category, String quantity, String productIcon, String price, String discountedPrice, String timestamp, String uid, String stock) {
        this.productID = productID;
        this.title = title;
        this.description = description;
        this.category = category;
        this.quantity = quantity;
        this.productIcon = productIcon;
        this.price = price;
        this.discountedPrice = discountedPrice;
        this.timestamp = timestamp;
        this.uid = uid;
        this.stock = stock;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getProductIcon() {
        return productIcon;
    }

    public void setProductIcon(String productIcon) {
        this.productIcon = productIcon;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDiscountedPrice() {
        return discountedPrice;
    }

    public void setDiscountedPrice(String discountedPrice) {
        this.discountedPrice = discountedPrice;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }
}
