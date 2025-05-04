package com.courcach.corsewww.Controllers.Client;

import com.courcach.Server.Services.ClassesForRequests.Orders;
import com.courcach.Server.Services.ClassesForRequests.ReportModel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Line;

public class MyReportRowController {
    private AnchorPane itemPane;
    private ReportModel reportModel;

    @FXML
    private TextArea myReportArea;

    @FXML
    private Label numberLabel;

    @FXML
    private Label priceLabel;

    @FXML
    private Label starsLabel;


    public void setTexts(ReportModel report) {
        this.reportModel = report;
        numberLabel.setText("№ "+report.getOrderNumber());
        priceLabel.setText(report.getOrder().getTotalPrice() + " BYN");
        myReportArea.setText(report.getReportMessange());
        for(int i = 0;i<report.getStars();i++){
            starsLabel.setText(starsLabel.getText() + "★");
        }
        if(report.getStars() == 1){
            starsLabel.setStyle("-fx-text-fill: red");
        }else if(report.getStars() > 1 && report.getStars() <= 3){
            starsLabel.setStyle("-fx-text-fill: #006a00");
        }else{
            starsLabel.setStyle("-fx-text-fill: #cfaa00");
        }
    }


    public void setText(ReportModel report) {
        this.reportModel = report;
        numberLabel.setText("№ "+report.getOrderNumber());
        priceLabel.setText(report.getOrder().getTotalPrice() + " BYN");
        myReportArea.setText(report.getReportAnswer());
        for(int i = 0;i<report.getStars();i++){
            starsLabel.setText(starsLabel.getText() + "★");
        }
        if(report.getStars() == 1){
            starsLabel.setStyle("-fx-text-fill: red");
        }else if(report.getStars() > 1 && report.getStars() <= 3){
            starsLabel.setStyle("-fx-text-fill: #006a00");
        }else{
            starsLabel.setStyle("-fx-text-fill: #cfaa00");
        }
    }

    public void setItemPane(AnchorPane itemPane) {this.itemPane = itemPane;}

    public ReportModel getReportModel() {return reportModel;}
}
