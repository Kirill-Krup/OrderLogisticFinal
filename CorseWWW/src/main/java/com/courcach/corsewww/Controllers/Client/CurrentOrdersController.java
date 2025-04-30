package com.courcach.corsewww.Controllers.Client;

import com.courcach.Server.Services.ClassesForRequests.Orders;
import com.courcach.Server.Services.ClassesForRequests.Places;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;

public class CurrentOrdersController {
    private AnchorPane itemPane;
    private Runnable cancelBut;

    @FXML
    private Label isPayment;

    @FXML
    private Button cancelOrderBut;

    @FXML
    private Label fieldForPlaces;

    @FXML
    private Label orderNumber;

    @FXML
    private Label price;

    @FXML
    private Label statusField;

    @FXML
    private Line statusLine;

    @FXML
    private ImageView cancelImage;

    @FXML
    private Label dateField;


    public void initialize() {
        cancelOrderBut.setOnAction(event -> {
            if(cancelBut!=null) {
                cancelBut.run();
            }
        });

    }

    public void setAllInfo(Orders order) {
        orderNumber.setText("№ " + order.getOrderNumber());
        price.setText(order.getTotalPrice() + " BYN");
        for(Places place : order.getOrderPlaces()){
            fieldForPlaces.setText(fieldForPlaces.getText()+ place.getPlaceName()+"\n");
        }
        dateField.setText(order.getDate().toString());
        statusField.setText(String.valueOf(order.getOrderStatus()));
        if(order.getOrderStatus() == Orders.OrderStatus.НОВЫЙ){
            statusLine.setStroke(Paint.valueOf("#449803"));
            statusField.setStyle("-fx-text-fill:#053a02 ");
        }else if(order.getOrderStatus()  == Orders.OrderStatus.В_ПРОЦЕССЕ){
            statusLine.setStroke(Paint.valueOf("#ef5902"));
            statusField.setStyle("-fx-text-fill: #ef5902 ");
        } else if (order.getOrderStatus()  == Orders.OrderStatus.ПРИНЯТ) {
            statusLine.setStroke(Paint.valueOf("#216307"));
            statusField.setStyle("-fx-text-fill: #216307 ");
        } else if (order.getOrderStatus() == Orders.OrderStatus.ОТМЕНЁН) {
            statusLine.setStroke(Paint.valueOf("#9e0909"));
            statusField.setStyle("-fx-text-fill: #9e0909 ");
        } else if (order.getOrderStatus() == Orders.OrderStatus.ОТКАЗАНО) {
            statusLine.setStroke(Paint.valueOf("#ff0000"));
            statusField.setStyle("-fx-text-fill: #ff0000 ");
        }else if(order.getOrderStatus()== Orders.OrderStatus.ВЫПОЛНЕН){
            statusLine.setStroke(Paint.valueOf("#156003"));
            statusField.setStyle("-fx-text-fill: #156003 ");
        }
        if(order.getTypeOfPayment().equals("Онлайн")){
            isPayment.setText("Оплачено");
            isPayment.setStyle("-fx-text-fill:#034e01");
        }else{
            isPayment.setText("Не оплачено");
            isPayment.setStyle("-fx-text-fill:#850909");
        }
    }

    public void setItemPane(AnchorPane itemPane) {this.itemPane = itemPane;}

    public void setCancelBut(Runnable cancelBut) {this.cancelBut = cancelBut;}

    public void setButDisable() {
        cancelOrderBut.setDisable(true);
        cancelOrderBut.setStyle("-fx-background-color: transparent");
        cancelImage.setVisible(false);
        statusField.setTranslateX(100);
        statusField.setStyle(" -fx-font-size: 20px;");
    }
}
