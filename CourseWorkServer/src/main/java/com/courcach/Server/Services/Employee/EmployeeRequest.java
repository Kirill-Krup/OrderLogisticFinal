package com.courcach.Server.Services.Employee;

import com.courcach.Server.Services.ClassesForRequests.ReportModel;

import java.io.Serializable;

public class EmployeeRequest implements Serializable {
    private final String request;
    private int orderNumber;
    private ReportModel report;
    private String employeeName;

    public EmployeeRequest(String request) {
        this.request = request;
    }

    public EmployeeRequest(String request, ReportModel report, String employeeName) {
        this.request = request;
        this.report = report;
        this.employeeName = employeeName;
    }

    public  EmployeeRequest(String request,int orderNumber, String employeeName) {
        this.request = request;
        this.orderNumber = orderNumber;
        this.employeeName = employeeName;
    }


    public String getRequest() {return request;}
    public int getOrderNumber() {return orderNumber;}
    public ReportModel getReport() {return report;}
    public String getEmployeeName() {return employeeName;}
}
