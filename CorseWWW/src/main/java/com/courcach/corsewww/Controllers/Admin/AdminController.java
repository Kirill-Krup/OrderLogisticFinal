package com.courcach.corsewww.Controllers.Admin;

import com.courcach.Server.Services.Admin.AdminRequest;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import com.courcach.corsewww.Models.Model;

public class AdminController {

    @FXML
    private Button checkOrdersBut;

    @FXML
    private Button placesBut;

    @FXML
    private Button editUsersBut;

    @FXML
    private Button marksBut;

    @FXML
    private Button exitBut;

    @FXML
    private Text textName;

    @FXML
    private Button logsButton;

    public void initialize() {
        textName.setText(Model.getInstance().getCurrentUser().getLogin());
        checkOrdersBut.setOnAction((event) -> {
            Stage stage = (Stage) checkOrdersBut.getScene().getWindow();
            Model.getInstance().getViewFactory().closeStage(stage);
            Model.getInstance().getViewFactory().showOrdersForAdminWindow();
        });

        placesBut.setOnAction((event) -> {
           Stage stage = (Stage) placesBut.getScene().getWindow();
            Model.getInstance().getViewFactory().showEditMaterialWindow();
           Model.getInstance().getViewFactory().closeStage(stage);
        });

        editUsersBut.setOnAction((event) -> {
            Stage stage = (Stage) editUsersBut.getScene().getWindow();
            Model.getInstance().getViewFactory().closeStage(stage);
            Model.getInstance().getViewFactory().showEditUsersWindow();
        });

        marksBut.setOnAction((event) -> {
           Stage stage = (Stage) marksBut.getScene().getWindow();
           Model.getInstance().getViewFactory().closeStage(stage);
           Model.getInstance().getViewFactory().showMarksWindow();
        });

        logsButton.setOnAction((event) -> {
            Stage stage = (Stage) logsButton.getScene().getWindow();
            Model.getInstance().getViewFactory().closeStage(stage);
            Model.getInstance().getViewFactory().showLogsWindow();
        });

        exitBut.setOnAction((event) -> {
            Model.getInstance().getConnectionToServer().sendObject(new AdminRequest("exit"));
            Stage stage = (Stage) exitBut.getScene().getWindow();
            Model.getInstance().getViewFactory().closeStage(stage);
            Model.getInstance().getViewFactory().showLoginWindow();
        });
    }
}
