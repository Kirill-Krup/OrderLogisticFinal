package com.courcach.Server.Services.Client;

import com.courcach.Server.Services.ClassesForRequests.Orders;

import java.io.Serializable;

public class ClientRequest implements Serializable {
    private final String request;
    private Float sum;
    private Orders order;

    public ClientRequest(String request) {
        this.request = request;
    }

    public ClientRequest(String request,Float sum) {
        this.request = request;
        this.sum = sum;
    }

    public ClientRequest(String request,Orders order) {
        this.request = request;
        this.order = order;
    }

    public String getRequest() {return request;}
    public Float getSum() {return sum;}
    public Orders getOrder() {return order;}
}
