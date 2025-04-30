package com.courcach.Server.Services.Employee;

import com.courcach.Server.Services.ClassesForRequests.ReportModel;

import java.io.Serializable;

public class EmployeeRequest implements Serializable {
    private final String request;
    private int orderNumber;
    private ReportModel report;

    public EmployeeRequest(String request) {
        this.request = request;
    }

    public EmployeeRequest(String request, ReportModel report) {
        this.request = request;
        this.report = report;
    }

    public  EmployeeRequest(String request,int orderNumber) {
        this.request = request;
        this.orderNumber = orderNumber;
    }


    public String getRequest() {return request;}
    public int getOrderNumber() {return orderNumber;}
    public ReportModel getReport() {return report;}
}
