package com.courcach.corsewww.Controllers.Admin;

import com.courcach.Server.Services.Admin.AdminRequest;
import com.courcach.Server.Services.ClassesForRequests.Log;
import com.courcach.corsewww.Models.ConnectionToServer;
import com.courcach.corsewww.Models.Model;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.List;

public class LogsController {
    private ObservableList<Log> logs = FXCollections.observableArrayList();
    @FXML
    private TableColumn<Log, String> dateColumn;

    @FXML
    private Button exitButton;

    @FXML
    private TableColumn<Log, String> logColumn;

    @FXML
    private TableColumn<Log, String> loginColumn;

    @FXML
    private TableView<Log> logsTable;

    public void initialize() {
        ConnectionToServer conn = Model.getInstance().getConnectionToServer();
        conn.sendObject(new AdminRequest("giveMeAllLogs"));
        List<Log> allLogs = (List<Log>) conn.receiveObject();
        logs.addAll(allLogs);
        loginColumn.setCellValueFactory(new PropertyValueFactory<>("login"));
        logColumn.setCellValueFactory(new PropertyValueFactory<>("log"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        logsTable.setItems(logs);

        exitButton.setOnAction(event -> {
            Stage stage = (Stage) exitButton.getScene().getWindow();
            Model.getInstance().getViewFactory().closeStage(stage);
            Model.getInstance().getViewFactory().showAdminWindow();
        });
    }

}
