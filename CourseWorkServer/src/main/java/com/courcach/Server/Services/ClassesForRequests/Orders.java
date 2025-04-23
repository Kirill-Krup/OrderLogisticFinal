package com.courcach.Server.Services.ClassesForRequests;

import java.util.List;

public class Orders {
    private int orderNumber;
    private float totalPrice;
    private int userID;
    private List<Places> orderPlaces;

    public int getOrderNumber() {return orderNumber;}
    public float getTotalPrice() {return totalPrice;}
    public int getUserID() {return userID;}
    public List<Places> getOrderPlaces() {return orderPlaces;}

    public void setOrderPlaces(List<Places> orderPlaces) {this.orderPlaces = orderPlaces;}
    public void setTotalPrice(float totalPrice) {this.totalPrice = totalPrice;}
    public void setOrderNumber(int orderNumber) {this.orderNumber = orderNumber;}
    public void setUserID(int userID) {this.userID = userID;}
}
