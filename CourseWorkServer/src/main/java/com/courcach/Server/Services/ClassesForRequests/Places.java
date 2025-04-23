package com.courcach.Server.Services.ClassesForRequests;

import java.io.Serializable;

public class Places implements Serializable {
    String placeName;
    String description;
    int quantity;
    float price;
    String category;

    public Places(){}

    public Places(String placeName, String description, float price, String category, int quantity) {
        this.placeName = placeName;
        this.description = description;
        this.price = price;
        this.category = category;
        this.quantity = quantity;
    }

    public String getPlaceName() {return placeName;}
    public String getDescription() {return description;}
    public float getPrice() {return price;}
    public String getCategory() {return category;}
    public int getQuantity() {return quantity;}

    public void setPlaceName(String placeName) {this.placeName = placeName;}
    public void setDescription(String description) {this.description = description;}
    public void setPrice(Float price) {this.price = price;}
    public void setCategory(String category) {this.category = category;}
    public void setQuantity(Integer quantity) {this.quantity = quantity;}
}
