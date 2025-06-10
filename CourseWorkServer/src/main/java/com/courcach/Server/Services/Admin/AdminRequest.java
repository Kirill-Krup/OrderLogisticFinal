package com.courcach.Server.Services.Admin;

import com.courcach.Server.Services.ClassesForRequests.Orders;
import com.courcach.Server.Services.ClassesForRequests.Places;
import com.courcach.Server.Services.ClassesForRequests.ReportModel;
import com.courcach.Server.Services.ClassesForRequests.Users;

import java.io.Serializable;
import java.time.LocalDate;

public class AdminRequest implements Serializable {
    private final String request;
    private String category;
    private Users user;
    private Places place;
    private Places selectedPlace;
    private Orders order;
    private LocalDate firstDate;
    private LocalDate lastDate;
    private ReportModel report;
    private String adminLogin;

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

    public AdminRequest(String request, Orders order){
        this.request = request;
        this.order = order;
    }

    public AdminRequest(String request, LocalDate firstDate, LocalDate lastDate){
        this.request = request;
        this.firstDate = firstDate;
        this.lastDate = lastDate;
    }

    public AdminRequest(String request, Users user, String adminLogin ){
        this.request = request;
        this.user = user;
        this.adminLogin = adminLogin;
    }

    public String getRequest() {return request;}
    public Users getUser() {return user;}
    public Places getPlace() {return place;}
    public String getCategory() {return category;}
    public Places getSelectedPlace() {return selectedPlace;}
    public Orders getOrder() {return order;}
    public LocalDate getFirstDate() {return firstDate;}
    public LocalDate getLastDate() {return lastDate;}
    public ReportModel getReport() {return report;}
    public String getAdminLogin() {return adminLogin;}
}
