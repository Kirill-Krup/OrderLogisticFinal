package com.courcach.Server.Services.Admin;

import com.courcach.Server.Services.ClassesForRequests.Places;
import com.courcach.Server.Services.ClassesForRequests.Users;

import java.io.Serializable;

public class AdminRequest implements Serializable {
    private final String request;
    private String category;
    private Users user;
    private Places place;
    private Places selectedPlace;

    public AdminRequest(String request) {
        this.request = request;
    }

    public AdminRequest(String request, Users user) {
        this.request = request;
        this.user = user;
    }

    public AdminRequest(String request,String category){
        this.request = request;
        this.category = category;
    }

    public AdminRequest(String request, Places place) {
        this.request = request;
        this.place = place;
    }

    public AdminRequest(String request,Places place, Places place1) {
        this.request = request;
        this.place = place;
        this.selectedPlace = place1;
    }

    public String getRequest() {return request;}
    public Users getUser() {return user;}
    public Places getPlace() {return place;}
    public String getCategory() {return category;}
    public Places getSelectedPlace() {return selectedPlace;}
}
