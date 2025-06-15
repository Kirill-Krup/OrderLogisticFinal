package com.courcach.corsewww.Controllers.Employee;

import com.courcach.Server.Services.ClassesForRequests.Orders;
import com.courcach.Server.Services.ClassesForRequests.Places;
import com.courcach.Server.Services.Employee.EmployeeRequest;
import com.courcach.corsewww.Models.ConnectionToServer;
import com.courcach.corsewww.Models.Model;
import com.courcach.corsewww.Views.NotificationUtil;
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
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

public class ClientOrdersForEmployeeController {
    private List<Places> allPlaces;
    private List<Orders> allOrders;
    ConnectionToServer connection = Model.getInstance().getConnectionToServer();

    @FXML
    private Button backBut;

    @FXML
    private DatePicker firstDate;

    @FXML
    private DatePicker lastDate;

    @FXML
    private ListView<AnchorPane> listWithOrders;

    @FXML
    private TextField searchField;

    @FXML
    private StackPane notificationPane;



    public void initialize() {
        connection.sendObject(new EmployeeRequest("giveMeAllOrdersAndPlaces"));
        allOrders =(List<Orders>) connection.receiveObject();
        allPlaces =(List<Places>) connection.receiveObject();
        refreshListWithOrders();

        backBut.setOnAction(event -> {
            Stage stage = (Stage) backBut.getScene().getWindow();
            Model.getInstance().getViewFactory().closeStage(stage);
            Model.getInstance().getViewFactory().showEmployeeWindow();
        });

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {applyFilters();});
        firstDate.valueProperty().addListener((observable, oldValue, newValue) -> {applyFilters();});
        lastDate.valueProperty().addListener((observable, oldValue, newValue) -> {applyFilters();});
    }

    private void applyFilters() {
        listWithOrders.getItems().clear();
        String search = searchField.getText().trim();
        boolean hasSearch = !search.isEmpty();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for(Orders order : allOrders) {
            if(hasSearch && !String.valueOf(order.getOrderNumber()).contains(search)) continue;
            LocalDate orderDate;
            try {
                orderDate = LocalDate.parse(order.getDate(), formatter);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            if (firstDate.getValue() != null && orderDate.isBefore(firstDate.getValue())) continue;
            if (lastDate.getValue() != null && orderDate.isAfter(lastDate.getValue())) continue;
            addItem(order);
        }
    }

    private void refreshListWithOrders() {
        listWithOrders.getItems().clear();
        sortFunc();
        for (Orders order : allOrders) {
            addItem(order);
        }
    }

    private void addItem(Orders order) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Fxml/Employee/LineOfOrder.fxml"));
        AnchorPane cellForList = null;
        try{
            cellForList=fxmlLoader.load();
        }catch(IOException e){
            e.printStackTrace();
        }
        LineOfOrderController lineOfOrderController = fxmlLoader.getController();
        lineOfOrderController.fillAll(order.getOrderNumber(),order.getTypeOfPayment(),order.getUserLogin(),order.getTotalPrice(),order.getAddressOfDelivery(),order.getDate(),order.getOrderStatus(),order.getOrderPlaces(),allPlaces);
        lineOfOrderController.setItemPane(cellForList);
        lineOfOrderController.setAdd(()->{
            connection.sendObject(new EmployeeRequest("acceptOrder",order.getOrderNumber(),Model.getInstance().getCurrentUser().getLogin()));
            allPlaces =(List<Places>) connection.receiveObject();
            order.setOrderStatus(Orders.OrderStatus.ПРИНЯТ);
            refreshListWithOrders();
        });
        lineOfOrderController.setRefusal(()->{
            connection.sendObject(new EmployeeRequest("refusalOrder",order.getOrderNumber(),Model.getInstance().getCurrentUser().getLogin()));
            if(order.getTypeOfPayment().equals("Онлайн")){
                NotificationUtil.showNotification(notificationPane,"Отказано в заказе, деньги возвращены клиенту");
            }else{
                NotificationUtil.showNotification(notificationPane,"Отказано в заказе № "+ order.getOrderNumber());
            }
            order.setOrderStatus(Orders.OrderStatus.ОТКАЗАНО);
            refreshListWithOrders();
        });
        listWithOrders.getItems().add(cellForList);
    }
    private void sortFunc(){
        Collections.sort(allOrders,(o1,o2)->{
            if(o1.getOrderStatus() == Orders.OrderStatus.НОВЫЙ && o2.getOrderStatus() != Orders.OrderStatus.НОВЫЙ){return -1;}
            else if (o1.getOrderStatus() != Orders.OrderStatus.НОВЫЙ && o2.getOrderStatus() == Orders.OrderStatus.НОВЫЙ) {return 1;}
            return 0;
        });
    }
}