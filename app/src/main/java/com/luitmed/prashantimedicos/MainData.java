package com.luitmed.prashantimedicos;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

//define table name
@Entity(tableName = "cart")
public class MainData implements Serializable {

    @PrimaryKey
    private long id;

    @ColumnInfo(name = "PID")
    private  String pid;

    @ColumnInfo(name = "Name")
    private  String name;

    @ColumnInfo(name = "OriginalPrice")
    private  String originalPrice;

    @ColumnInfo(name = "PriceEach")
    private String priceEach;

    @ColumnInfo(name = "Price")
    private  String price;

    @ColumnInfo(name = "Quantity")
    private  String quantity;

    @ColumnInfo(name = "Number")
    private  String number;

    @ColumnInfo(name = "Image")
    private  String image;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPriceEach() {
        return priceEach;
    }

    public void setPriceEach(String priceEach) {
        this.priceEach = priceEach;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


    public String getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(String originalPrice) {
        this.originalPrice = originalPrice;
    }

}
