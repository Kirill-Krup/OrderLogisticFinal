package com.courcach.corsewww.Controllers.Admin;

import com.courcach.Server.Services.Admin.AdminRequest;
import com.courcach.Server.Services.ClassesForRequests.Orders;
import com.courcach.Server.Services.Client.ClientRequest;
import com.courcach.corsewww.Controllers.Client.CurrentOrdersController;
import com.courcach.corsewww.Controllers.Client.HistoryRowController;
import com.courcach.corsewww.Models.ConnectionToServer;
import com.courcach.corsewww.Models.Model;
import com.courcach.corsewww.Views.NotificationUtil;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class CheckOrdersController {
    private List<Orders> allHistory;
    ConnectionToServer connect = Model.getInstance().getConnectionToServer();
    private FilteredList<Orders> filteredHistory;

    @FXML
    private Button activeBut;

    @FXML
    private Button backBut;

    @FXML
    private DatePicker firstDate;

    @FXML
    private DatePicker lastDate;

    @FXML
    private ListView<AnchorPane> listForHistory;

    @FXML
    private Button othersBut;

    @FXML
    private TextField searchField;

    @FXML
    private StackPane notificationPane;

    public void initialize() {
        connect.sendObject(new AdminRequest("giveMeAllOrders"));
        allHistory=(List<Orders>) connect.receiveObject();
        filteredHistory = new FilteredList<>(FXCollections.observableArrayList(allHistory), p -> true);
        refreshList();


        backBut.setOnAction(e -> {
            Stage stage = (Stage) backBut.getScene().getWindow();
            Model.getInstance().getViewFactory().closeStage(stage);
            Model.getInstance().getViewFactory().showAdminWindow();
        });

        activeBut.setOnAction(e -> {
           refreshList();
            othersBut.setStyle("-fx-background-color: transparent;");
            activeBut.setStyle("-fx-background-color: green;");
        });

        othersBut.setOnAction(e -> {
           refreshOthersList();
           othersBut.setStyle("-fx-background-color: green;");
           activeBut.setStyle("-fx-background-color: transparent;");
        });
        setupSearch();
        firstDate.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                LocalDate endDate = lastDate.getValue() != null
                        ? lastDate.getValue()
                        : LocalDate.now();

                connect.sendObject(new AdminRequest("giveMeOrdersInDates", newValue, endDate));
                List<Orders> ordersInRange = (List<Orders>) connect.receiveObject();
                filteredHistory = new FilteredList<>(FXCollections.observableArrayList(ordersInRange), p -> true);
                if(activeBut.getStyle().contains("green")){
                    refreshList();
                }else{
                    refreshOthersList();
                }
            }
        });

        lastDate.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                LocalDate startDate = firstDate.getValue() != null
                        ? firstDate.getValue()
                        : LocalDate.of(2024, 1, 1);

                connect.sendObject(new AdminRequest("giveMeOrdersInDates", startDate, newValue));
                List<Orders> ordersInRange = (List<Orders>) connect.receiveObject();

                filteredHistory = new FilteredList<>(FXCollections.observableArrayList(ordersInRange), p -> true);
                if(!activeBut.getStyle().contains("green")){
                     refreshOthersList();
                }else{
                    refreshList();
                }
            }
        });
    }


    private void refreshList() {
        filteredHistory.setPredicate(order ->
                order.getOrderStatus() != Orders.OrderStatus.ОТКАЗАНО &&
                        order.getOrderStatus() != Orders.OrderStatus.ОТМЕНЁН &&
                        order.getOrderStatus() != Orders.OrderStatus.ВЫПОЛНЕН
        );
        refreshFilteredList();
    }

    private void refreshOthersList() {
        filteredHistory.setPredicate(order ->
                order.getOrderStatus() != Orders.OrderStatus.ПРИНЯТ &&
                        order.getOrderStatus() != Orders.OrderStatus.В_ПРОЦЕССЕ &&
                        order.getOrderStatus() != Orders.OrderStatus.НОВЫЙ
        );
        refreshFilteredList();
    }

    private void addItemForActive(Orders order){
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
        if(order.getOrderStatus() == Orders.OrderStatus.НОВЫЙ || order.getOrderStatus() == Orders.OrderStatus.ПРИНЯТ || order.getOrderStatus() == Orders.OrderStatus.В_ПРОЦЕССЕ){
            currentOrdersController.setButDisable();
        }else{
            currentOrdersController.setCancelBut(()->{
                connect.sendObject(new AdminRequest("deleteOrder",order));
                NotificationUtil.showNotification(notificationPane,"Заказ №"+order.getOrderNumber()+" успешно удалён");
                allHistory.remove(order);
                refreshOthersList();
            });
        }
        listForHistory.getItems().add(cellForOrder);
    }


    private void setupSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                if (activeBut.getStyle().contains("green")) {
                    refreshList();
                } else {
                    refreshOthersList();
                }
                return;
            }
            filteredHistory.setPredicate(order -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();

                if (order.getUserLogin() != null && order.getUserLogin().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (order.getOrderStatus() != null && order.getOrderStatus().toString().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (String.valueOf(order.getOrderNumber()).contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });
            refreshFilteredList();
        });
    }

    private void refreshFilteredList() {
        listForHistory.getItems().clear();
        for (Orders order : filteredHistory) {
            addItemForActive(order);
        }
    }

}
