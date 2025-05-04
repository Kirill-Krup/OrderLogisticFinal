package com.courcach.corsewww.Controllers.Admin;

import com.courcach.Server.Services.Admin.AdminRequest;
import com.courcach.Server.Services.ClassesForRequests.ReportModel;
import com.courcach.Server.Services.Client.ClientRequest;
import com.courcach.corsewww.Models.ConnectionToServer;
import com.courcach.corsewww.Models.Model;
import com.courcach.corsewww.Views.NotificationUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.List;
import java.util.Stack;

public class MarksController {
    private List<ReportModel> allReports;

    @FXML
    private Button backBut;

    @FXML
    private TableColumn<ReportModel, String> clientReportColumn;

    @FXML
    private TableColumn<ReportModel, String> employeeResponseColumn;

    @FXML
    private TableColumn<ReportModel, String> loginColumn;

    @FXML
    private TableColumn<ReportModel, Integer> starsColumn;

    @FXML
    private TableView<ReportModel> tableForComments;




    public void initialize() {
        ConnectionToServer connect = Model.getInstance().getConnectionToServer();
        connect.sendObject(new AdminRequest("giveMeAllReports"));
        allReports =(List<ReportModel>) connect.receiveObject();
        loginColumn.setCellValueFactory(new PropertyValueFactory<>("userLogin"));
        clientReportColumn.setCellValueFactory(new PropertyValueFactory<>("reportMessange"));
        employeeResponseColumn.setCellValueFactory(new PropertyValueFactory<>("reportAnswer"));
        starsColumn.setCellValueFactory(new PropertyValueFactory<>("stars"));
        tableForComments.getItems().setAll(allReports);
        backBut.setOnAction(event -> {
            Stage stage = (Stage) backBut.getScene().getWindow();
            Model.getInstance().getViewFactory().closeStage(stage);
            Model.getInstance().getViewFactory().showAdminWindow();
        });
    }
}
