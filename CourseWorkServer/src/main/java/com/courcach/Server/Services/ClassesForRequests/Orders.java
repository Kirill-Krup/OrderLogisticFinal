package com.courcach.Server.Services.ClassesForRequests;

import java.io.Serializable;
import java.util.List;

public class Orders implements Serializable {
    private int orderNumber;
    private String userLogin;
    private float totalPrice;
    private String FIO;
    private String phone;
    private String typeOfPayment;
    private String typeOfDelivery;
    private String addressOfDelivery;
    private List<Places> orderPlaces;
    private String date;
    private OrderStatus orderStatus;

    public enum OrderStatus {
        НОВЫЙ,
        ПРИНЯТ,
        В_ПРОЦЕССЕ,
        ВЫПОЛНЕН,
        ОТМЕНЁН,
        ОТКАЗАНО
    }

    public Orders() {}

    public Orders(int orderNumber,String userLogin,float totalPrice,String FIO,String phone,String typeOfPayment,String typeOfDelivery,String addressOfDelivery,List<Places> orderPlaces,String date,OrderStatus orderStatus) {
        this.orderNumber = orderNumber;
        this.userLogin = userLogin;
        this.totalPrice = totalPrice;
        this.FIO = FIO;
        this.phone = phone;
        this.typeOfPayment = typeOfPayment;
        this.typeOfDelivery = typeOfDelivery;
        this.addressOfDelivery = addressOfDelivery;
        this.orderPlaces = orderPlaces;
        this.date = date;
        this.orderStatus = orderStatus;
    }

    public int getOrderNumber() {return orderNumber;}
    public String getUserLogin() {return userLogin;}
    public float getTotalPrice() {return totalPrice;}
    public String getFIO() {return FIO;}
    public String getPhone() {return phone;}
    public String getTypeOfPayment() {return typeOfPayment;}
    public String getTypeOfDelivery() {return typeOfDelivery;}
    public String getAddressOfDelivery() {return addressOfDelivery;}
    public List<Places> getOrderPlaces() {return orderPlaces;}
    public OrderStatus getOrderStatus() {return orderStatus;}
    public String getDate() {return date;}


    public void setOrderNumber(int orderNumber) {this.orderNumber = orderNumber;}
    public void setUserLogin(String userLogin) {this.userLogin = userLogin;}
    public void setTotalPrice(float totalPrice) {this.totalPrice = totalPrice;}
    public void setFIO(String FIO) {this.FIO = FIO;}
    public void setPhone(String phone) {this.phone = phone;}
    public void setTypeOfPayment(String typeOfPayment) {this.typeOfPayment = typeOfPayment;}
    public void setTypeOfDelivery(String typeOfDelivery) {this.typeOfDelivery = typeOfDelivery;}
    public void setAddressOfDelivery(String addressOfDelivery) {this.addressOfDelivery = addressOfDelivery;}
    public void setOrderPlaces(List<Places> orderPlaces) {this.orderPlaces = orderPlaces;}
    public void setOrderStatus(OrderStatus orderStatus) {this.orderStatus = orderStatus;}
    public void setDate(String date) {this.date = date;}
}
