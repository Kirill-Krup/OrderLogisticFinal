package com.courcach.Server.Services.Admin;

import com.courcach.Server.Services.ClassesForRequests.Users;

import java.io.Serializable;

public class AdminRequest implements Serializable {
    private final String request;
    private Users user;

    public AdminRequest(String request) {
        this.request = request;
    }

    public AdminRequest(String request, Users user) {
        this.request = request;
        this.user = user;
    }

    public String getRequest() {return request;}
    public Users getUser() {return user;}
}
