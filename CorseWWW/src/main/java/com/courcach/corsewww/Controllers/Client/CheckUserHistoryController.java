package com.courcach.corsewww.Controllers.Client;

import com.courcach.Server.Services.Client.ClientRequest;
import com.courcach.corsewww.Models.Model;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class CheckUserHistoryController {
    @FXML
    private Label MyWallet;

    @FXML
    private Button addNewOrder;

    @FXML
    private Button backBut;

    @FXML
    private Button checkMyOrder;

    @FXML
    private Button checkUserHistory;

    @FXML
    private Button exitBut;

    @FXML
    private Button homeText;

    @FXML
    private VBox menuBox;

    @FXML
    private Button menuBut;

    @FXML
    private Button openWallet;

    @FXML
    private HBox topPane;


    public void initialize() {
        initial();
    }


    private void initial(){
        menuBox.setMouseTransparent(true);
        MyWallet.setText(String.valueOf(Model.getInstance().getCurrentUser().getWallet()));
        menuBut.setOnAction(e -> {
            menuBox.setOpacity(1);
            topPane.setStyle("-fx-background-color: rgba(0,0,0,0.8)");
            //mainPane.setStyle("-fx-background-color: rgba(0,0,0,0.8)");
            menuBox.setMouseTransparent(false);
        });

        //Menu section
        addNewOrder.setOnAction(e -> {
            Stage stage = (Stage) addNewOrder.getScene().getWindow();
            Model.getInstance().getViewFactory().closeStage(stage);
            Model.getInstance().getViewFactory().showAddNewOrderWindow();
        });

        openWallet.setOnAction(e -> {
            Stage stage = (Stage) openWallet.getScene().getWindow();
            Model.getInstance().getViewFactory().closeStage(stage);
            Model.getInstance().getViewFactory().showWalletWindow();
        });

        backBut.setOnAction(e -> {
            menuBox.setOpacity(0);
            menuBox.setMouseTransparent(true);
            topPane.setStyle("-fx-background-color: rgba(0,0,0,0.5)");
            //mainPane.setStyle("-fx-background-color: transparent");
        });

        homeText.setOnAction(e -> {
            Stage stage = (Stage) homeText.getScene().getWindow();
            Model.getInstance().getViewFactory().closeStage(stage);
            Model.getInstance().getViewFactory().showClientWindow();
        });
        exitBut.setOnAction(e -> {
            Model.getInstance().getConnectionToServer().sendObject(new ClientRequest("exit"));
            Stage stage = (Stage) menuBut.getScene().getWindow();
            Model.getInstance().getViewFactory().closeStage(stage);
            Model.getInstance().getViewFactory().showLoginWindow();
        });
    }
}
