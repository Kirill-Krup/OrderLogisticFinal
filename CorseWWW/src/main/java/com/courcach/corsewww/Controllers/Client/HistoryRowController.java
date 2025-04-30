package com.courcach.corsewww.Controllers.Client;

import com.courcach.Server.Services.ClassesForRequests.Orders;
import com.courcach.Server.Services.ClassesForRequests.Places;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;

public class HistoryRowController {
    private AnchorPane itemPane;
    private Orders order;
    @FXML
    private Label DateField;

    @FXML
    private Line line1;

    @FXML
    private Line line2;

    @FXML
    private Label orderNumberField;

    @FXML
    private Label orderPlacesField;

    @FXML
    private Label priceField;

    @FXML
    private Label statusField;
    
    public void setTexts(Orders order) {
        this.order = order;
        DateField.setText(order.getDate());
        orderNumberField.setText("№ "+order.getOrderNumber());
        priceField.setText(String.valueOf(order.getTotalPrice()));
        statusField.setText(order.getOrderStatus().toString());
        for(Places place : order.getOrderPlaces()){
            orderPlacesField.setText(orderPlacesField.getText() + place.getPlaceName() + "\n");
        }
        if(order.getOrderStatus() == Orders.OrderStatus.НОВЫЙ){
            line1.setStroke(Paint.valueOf("#449803"));
            line2.setStroke(Paint.valueOf("#449803"));
            statusField.setStyle("-fx-text-fill:#053a02 ");
        }else if(order.getOrderStatus()  == Orders.OrderStatus.В_ПРОЦЕССЕ){
            line1.setStroke(Paint.valueOf("#025e6a"));
            line2.setStroke(Paint.valueOf("#025e6a"));
            statusField.setStyle("-fx-text-fill: #ef5902 ");
        }else if(order.getOrderStatus()  == Orders.OrderStatus.ОТМЕНЁН){
            line1.setStroke(Paint.valueOf("#9f0000"));
            line2.setStroke(Paint.valueOf("#9f0000"));
            statusField.setStyle("-fx-text-fill: #700000 ");
        }else if(order.getOrderStatus()  == Orders.OrderStatus.ВЫПОЛНЕН){
            line1.setStroke(Paint.valueOf("#0a4c00"));
            line2.setStroke(Paint.valueOf("#0a4c00"));
            statusField.setStyle("-fx-text-fill: #00af05 ");
        } else if (order.getOrderStatus()  == Orders.OrderStatus.ПРИНЯТ) {
            line1.setStroke(Paint.valueOf("#ef5902"));
            line2.setStroke(Paint.valueOf("#ef5902"));
            statusField.setStyle("-fx-text-fill: #216307 ");
        }else if (order.getOrderStatus()  == Orders.OrderStatus.ОТКАЗАНО) {
            line1.setStroke(Paint.valueOf(String.valueOf(Color.RED)));
            line2.setStroke(Paint.valueOf(String.valueOf(Color.RED)));
        }
    }

    public void setItemPane(AnchorPane cellForOrder) {this.itemPane = cellForOrder;}

    public Orders getOrder() {return order;}
}
