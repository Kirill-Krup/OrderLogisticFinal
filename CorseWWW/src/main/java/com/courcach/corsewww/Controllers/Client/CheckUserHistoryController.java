package com.courcach.corsewww.Controllers.Client;

import com.courcach.Server.Services.ClassesForRequests.Orders;
import com.courcach.Server.Services.Client.ClientRequest;
import com.courcach.corsewww.Models.ConnectionToServer;
import com.courcach.corsewww.Models.Model;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class CheckUserHistoryController {
    List<Orders> allHistory;

    @FXML
    private Label MyWallet;

    @FXML
    private Button addNewOrder;

    @FXML
    private Button backBut;

    @FXML
    private Button checkMyOrder;

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

    @FXML
    private ListView<AnchorPane> listForHistory;

    @FXML
    private Pane mainPane;


    public void initialize() {
        initial();
        ConnectionToServer connect = Model.getInstance().getConnectionToServer();
        connect.sendObject(new ClientRequest("giveMeAllMyOrders",Model.getInstance().getCurrentUser()));
        allHistory =(List<Orders>) connect.receiveObject();
        refreshList();
    }

    private void refreshList(){
        listForHistory.getItems().clear();
        for(Orders order : allHistory){
            addItem(order);
        }
    }

    private void addItem(Orders order){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Fxml/Client/HistoryRow.fxml"));
        AnchorPane cellForOrder;
        try {
            cellForOrder = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        HistoryRowController controller = fxmlLoader.getController();
        controller.setTexts(order);
        controller.setItemPane(cellForOrder);
        listForHistory.getItems().add(cellForOrder);
    }

    private void initial(){
        menuBox.setMouseTransparent(true);
        MyWallet.setText(String.valueOf(Model.getInstance().getCurrentUser().getWallet()));
        menuBut.setOnAction(e -> {
            menuBox.setOpacity(1);
            topPane.setStyle("-fx-background-color: rgba(0,0,0,0.8)");
            mainPane.setStyle("-fx-background-color: rgba(0,0,0,0.8)");
            mainPane.setMouseTransparent(false);
            menuBox.setMouseTransparent(false);
        });

        //Menu section
        addNewOrder.setOnAction(e -> {
            Stage stage = (Stage) addNewOrder.getScene().getWindow();
            Model.getInstance().getViewFactory().closeStage(stage);
            Model.getInstance().getViewFactory().showAddNewOrderWindow();
        });

        checkMyOrder.setOnAction(event->{
            Stage stage = (Stage) checkMyOrder.getScene().getWindow();
            Model.getInstance().getViewFactory().closeStage(stage);
            Model.getInstance().getViewFactory().showCheckMyOrderWindow();
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
            mainPane.setMouseTransparent(true);
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
