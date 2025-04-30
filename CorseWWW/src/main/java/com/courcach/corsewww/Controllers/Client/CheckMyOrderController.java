package com.courcach.corsewww.Controllers.Client;

import com.courcach.Server.Services.ClassesForRequests.Orders;
import com.courcach.Server.Services.ClassesForRequests.Places;
import com.courcach.Server.Services.Client.ClientRequest;
import com.courcach.corsewww.Models.ConnectionToServer;
import com.courcach.corsewww.Models.Model;
import com.courcach.corsewww.Views.NotificationUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.List;
import java.util.Objects;

public class CheckMyOrderController {
    private List<Orders> activeOrders;
    ConnectionToServer connection = Model.getInstance().getConnectionToServer();

    @FXML
    private Label MyWallet;

    @FXML
    private Button addNewOrder;

    @FXML
    private Button backBut;

    @FXML
    private Button checkUserHistory;

    @FXML
    private Button doUWantNewOrderBut;

    @FXML
    private Button exitBut;

    @FXML
    private Button homeText;

    @FXML
    private ListView<AnchorPane> listForOrders;

    @FXML
    private Pane mainPane;

    @FXML
    private VBox menuBox;

    @FXML
    private Button menuBut;

    @FXML
    private Label noOrdersLabel;

    @FXML
    private ImageView noOrdersSmile;

    @FXML
    private Button openWallet;

    @FXML
    private HBox topPane;

    @FXML
    private StackPane notificationPane;

    public void initialize() {
        initial();
        connection.sendObject(new ClientRequest("giveMeMyActiveOrders",Model.getInstance().getCurrentUser()));
        activeOrders =(List<Orders>) connection.receiveObject();
        checkOrders();

        doUWantNewOrderBut.setOnAction(event -> {
            Stage stage = (Stage) doUWantNewOrderBut.getScene().getWindow();
            Model.getInstance().getViewFactory().closeStage(stage);
            Model.getInstance().getViewFactory().showAddNewOrderWindow();
        });
    }

    private void checkOrders() {
        if(activeOrders.isEmpty()) {
            noOrdersLabel.setOpacity(1);
            noOrdersSmile.setOpacity(1);
            doUWantNewOrderBut.setOpacity(1);
            doUWantNewOrderBut.setMouseTransparent(false);
        }else{
            refreshList();
        }
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

        openWallet.setOnAction(e -> {
            Stage stage = (Stage) openWallet.getScene().getWindow();
            Model.getInstance().getViewFactory().closeStage(stage);
            Model.getInstance().getViewFactory().showWalletWindow();
        });

        backBut.setOnAction(e -> {
            menuBox.setOpacity(0);
            menuBox.setMouseTransparent(true);
            mainPane.setMouseTransparent(true);
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

    private void refreshList(){
        listForOrders.getItems().clear();
        for(Orders order : activeOrders){
            addOrderCell(order);
        }
    }

    private void addOrderCell(Orders order){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Fxml/Client/CurrentOrders.fxml"));
        AnchorPane cellForOrder = null;
        try{
            cellForOrder = fxmlLoader.load();
        }catch(Exception e){
            e.printStackTrace();
        }
        CurrentOrdersController currentOrdersController = fxmlLoader.getController();
        currentOrdersController.setAllInfo(order);
        currentOrdersController.setItemPane(cellForOrder);
        currentOrdersController.setCancelBut(()->{
            connection.sendObject(new ClientRequest("cancelOrder",order.getOrderNumber(),Model.getInstance().getCurrentUser()));
            NotificationUtil.showNotification(notificationPane,"Ваш заказ успешно отменён");
            activeOrders =(List<Orders>) connection.receiveObject();
            refreshList();
        });
        listForOrders.getItems().add(cellForOrder);
    }
}
