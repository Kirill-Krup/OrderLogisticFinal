package com.courcach.Server.Services.ClassesForRequests;

import java.io.Serializable;
import java.sql.Time;
import java.sql.Timestamp;

public class ReportModel implements Serializable {
    private Timestamp timeOfReport;
    private String reportMessange;
    private String userLogin;
    private String reportAnswer;
    private Timestamp reportAnswerTime;
    private int orderNumber;
    private Orders order;
    private Boolean checked;
    private int stars;

    public ReportModel(Timestamp timeOfReport, String reportMessange, String userLogin, int orderNumber, int stars) {
        this.timeOfReport = timeOfReport;
        this.reportMessange = reportMessange;
        this.userLogin = userLogin;
        this.orderNumber = orderNumber;
        this.stars = stars;
    }

    public ReportModel(int orderNumber, String reportAnswer, Timestamp reportAnswerTime) {
        this.orderNumber = orderNumber;
        this.reportAnswer = reportAnswer;
        this.reportAnswerTime = reportAnswerTime;
    }

    public ReportModel(Timestamp timeOfReport, String reportMessange, String userLogin, String reportAnswer, Timestamp reportAnswerTime, int orderNumber, Orders order, Boolean checked,int stars) {
        this.timeOfReport = timeOfReport;
        this.reportMessange = reportMessange;
        this.userLogin = userLogin;
        this.reportAnswer = reportAnswer;
        this.reportAnswerTime = reportAnswerTime;
        this.orderNumber = orderNumber;
        this.order = order;
        this.checked = checked;
        this.stars = stars;
    }


    public Timestamp getTimeOfReport() {return timeOfReport;}
    public String getReportMessange() {return reportMessange;}
    public String getUserLogin() {return userLogin;}
    public int getOrderNumber() {return orderNumber;}
    public Orders getOrder() {return order;}
    public Timestamp getReportAnswerTime() {return reportAnswerTime;}
    public String getReportAnswer() {return reportAnswer;}
    public Boolean getChecked() {return checked;}
    public int getStars() {return stars;}


    public void setOrder(Orders order) {this.order = order;}
    public void setReportAnswerTime(Timestamp reportAnswerTime) {this.reportAnswerTime = reportAnswerTime;}
    public void setReportAnswer(String reportAnswer) {this.reportAnswer = reportAnswer;}
    public void setUserLogin(String userLogin) {this.userLogin = userLogin;}
    public void setOrderNumber(int orderNumber) {this.orderNumber = orderNumber;}
    public void setTimeOfReport(Timestamp timeOfReport) {this.timeOfReport = timeOfReport;}
    public void setReportMessange(String reportMessange) {this.reportMessange = reportMessange;}
    public void setChecked(Boolean checked) {this.checked = checked;}
    public void setStars(int stars) {this.stars = stars;}
}
