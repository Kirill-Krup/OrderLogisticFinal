package com.courcach.corsewww.Controllers.Employee;

import com.courcach.Server.Services.ClassesForRequests.Orders;
import com.courcach.Server.Services.ClassesForRequests.Places;
import com.courcach.Server.Services.Employee.EmployeeRequest;
import com.courcach.corsewww.Models.ConnectionToServer;
import com.courcach.corsewww.Models.Model;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
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
    }

    private void refreshListWithOrders() {
        listWithOrders.getItems().clear();
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
        lineOfOrderController.fillAll(order.getUserLogin(),order.getTotalPrice(),order.getAddressOfDelivery(),order.getDate(),order.getOrderStatus(),order.getOrderPlaces(),allPlaces);
        lineOfOrderController.setItemPane(cellForList);
        lineOfOrderController.setAdd(()->{
            connection.sendObject(new EmployeeRequest("acceptOrder",order.getOrderNumber()));
            allPlaces =(List<Places>) connection.receiveObject();
            order.setOrderStatus(Orders.OrderStatus.ПРИНЯТ);
            refreshListWithOrders();
        });
        lineOfOrderController.setRefusal(()->{
            connection.sendObject(new EmployeeRequest("refusalOrder",order.getOrderNumber()));
            order.setOrderStatus(Orders.OrderStatus.ОТКАЗАНО);
            refreshListWithOrders();
        });
        listWithOrders.getItems().add(cellForList);
    }
}
