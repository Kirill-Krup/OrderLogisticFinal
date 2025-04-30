package com.courcach.corsewww.Controllers.Employee;

import com.courcach.Server.Services.ClassesForRequests.ReportModel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;

public class ReportRowForEmployeeController {
    private AnchorPane itemPane;
    private ReportModel report;

    @FXML
    private TextArea areaWithReport;

    @FXML
    private Label dateOfReportField;

    @FXML
    private Label loginField;

    @FXML
    private Label orderNumberField;

    @FXML
    private Label starsLabel;


    public void setTexts(ReportModel report){
        this.report = report;
        orderNumberField.setText("№"+report.getOrderNumber());
        loginField.setText(report.getUserLogin());
        dateOfReportField.setText(String.valueOf(report.getTimeOfReport()));
        areaWithReport.setText(report.getReportMessange());
        for(int i = 0; i<report.getStars();i++){
            starsLabel.setText(starsLabel.getText() + "★");
        }
    }

    public void setItemPane(AnchorPane itemPane){this.itemPane = itemPane;}
    public ReportModel getReport(){return report;}
}
