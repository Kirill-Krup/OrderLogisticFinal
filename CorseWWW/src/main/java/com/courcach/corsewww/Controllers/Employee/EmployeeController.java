package com.courcach.corsewww.Controllers.Employee;

import com.courcach.Server.Services.Employee.EmployeeRequest;
import com.courcach.corsewww.Models.ConnectionToServer;
import com.courcach.corsewww.Models.Model;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class EmployeeController {
    private int counter = 0;
    private int counterRep = 0;

    @FXML
    private Label counterOfNewOrders;

    @FXML
    private Label counterOfNewReports;

    @FXML
    private Label employeeName;

    @FXML
    private Button exitBut;

    @FXML
    private Button workWithOrders;

    @FXML
    private Button workWithReportsBut;


    public void initialize() {
        ConnectionToServer connect = Model.getInstance().getConnectionToServer();
        connect.sendObject(new EmployeeRequest("giveMeNew"));
        counter =(Integer) connect.receiveObject();
        counterRep =(Integer) connect.receiveObject();
        initialText();
        workWithOrders.setOnAction(event -> {
            Stage stage = (Stage) workWithOrders.getScene().getWindow();
            Model.getInstance().getViewFactory().closeStage(stage);
            Model.getInstance().getViewFactory().showClientOrdersForEmployeeWindow();
        });

        workWithReportsBut.setOnAction(event -> {
          Stage stage = (Stage) workWithReportsBut.getScene().getWindow();
          Model.getInstance().getViewFactory().closeStage(stage);
          Model.getInstance().getViewFactory().showReportsWorkForEmployeeWindow();
        });

        exitBut.setOnAction(event -> {
            connect.sendObject(new EmployeeRequest("exit"));
            Stage stage = (Stage) exitBut.getScene().getWindow();
            Model.getInstance().getViewFactory().closeStage(stage);
            Model.getInstance().getViewFactory().showLoginWindow();
        });

    }

    private void initialText() {
        employeeName.setText(Model.getInstance().getCurrentUser().getLastName() + " " + Model.getInstance().getCurrentUser().getFirstName());
        counterOfNewOrders.setText(String.valueOf(counter));
        counterOfNewReports.setText(String.valueOf(counterRep));
    }

}
