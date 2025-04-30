package com.courcach.Server.Services.ClassesForRequests;

public class ModelForTable {
    private String  placeName;
    private int quantity;
    private int allQuantity;

    public ModelForTable(String placeName, int quantity, int allQuantity) {
        this.placeName = placeName;
        this.quantity = quantity;
        this.allQuantity = allQuantity;
    }

    public String getPlaceName() {return placeName;}
    public void setPlaceName(String placeName) {this.placeName = placeName;}
    public int getQuantity() {return quantity;}


    public void setQuantity(int quantity) {this.quantity = quantity;}
    public int getAllQuantity() {return allQuantity;}
    public void setAllQuantity(int allQuantity) {this.allQuantity = allQuantity;}
}
