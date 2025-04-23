package com.courcach.corsewww.Controllers.Client;

import com.courcach.Server.Services.ClassesForRequests.Places;
import com.courcach.Server.Services.Client.ClientRequest;
import com.courcach.corsewww.Models.ConnectionToServer;
import com.courcach.corsewww.Models.Model;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.List;
import java.util.Objects;

public class CheckMyOrderController {

    @FXML
    private ListView<Places> listForOrders;

    @FXML
    private Button menuBut;

    @FXML
    private Button backBut;

    @FXML
    private Button exitBut;

    @FXML
    private Button addNewOrder;

    @FXML
    private Pane mainPane;

    @FXML
    private Pane topPane;


    @FXML
    private VBox menuBox;

    @FXML
    private Button homeText;

    @FXML
    private Button openWallet;

    public void initialize() {
        initial();
    }


    private void initial(){
        menuBox.setMouseTransparent(true);
        menuBut.setOnAction(e -> {
            menuBox.setOpacity(1);
            topPane.setStyle("-fx-background-color: rgba(0,0,0,0.8)");
            mainPane.setStyle("-fx-background-color: rgba(0,0,0,0.8)");
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
            mainPane.setStyle("-fx-background-color: transparent");
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
