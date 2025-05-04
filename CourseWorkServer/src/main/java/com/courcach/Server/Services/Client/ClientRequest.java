package com.courcach.Server.Services.Client;

import com.courcach.Server.Services.ClassesForRequests.Orders;
import com.courcach.Server.Services.ClassesForRequests.ReportModel;
import com.courcach.Server.Services.ClassesForRequests.Users;

import java.io.Serializable;

public class ClientRequest implements Serializable {
    private final String request;
    private Float sum;
    private Orders order;
    private Users user;
    private int orderNumber;
    private ReportModel report;
    private double wallet;

    public ClientRequest(String request) {
        this.request = request;
    }

    public ClientRequest(String request,Users user,Float sum) {
        this.request = request;
        this.user = user;
        this.sum = sum;
    }

    public ClientRequest(String request,int orderNumber,Users user) {
        this.request = request;
        this.orderNumber = orderNumber;
        this.user = user;
    }

    public ClientRequest(String request,Orders order) {
        this.request = request;
        this.order = order;
    }

    public ClientRequest(String request,Users user) {
        this.request = request;
        this.user = user;
    }

    public ClientRequest(String request,ReportModel report) {
        this.request = request;
        this.report = report;
    }

    public ClientRequest(String request,Users user,Double wallet) {
        this.request = request;
        this.user = user;
        this.wallet = wallet;
    }

    public String getRequest() {return request;}
    public Float getSum() {return sum;}
    public Orders getOrder() {return order;}
    public Users getUser() {return user;}
    public int getOrderNumber() {return orderNumber;}
    public ReportModel getReport() {return report;}
    public double getWallet() {return wallet;}
}
