package com.courcach.corsewww.Controllers.Client;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class CartItemController {
    private AnchorPane itemPane;
    private Runnable delQuantity;
    private Runnable addQuantity;
    private Runnable delPlace;

    @FXML
    private Button addPlaceBut;

    @FXML
    private Button delOneQuantityBut;

    @FXML
    private Button deletePlaceFromCartBut;

    @FXML
    private Label descriptionCartField;

    @FXML
    private Label nameCartField;

    @FXML
    private Label priceCartField;

    @FXML
    private Label quantityCartField;


    public void initialize() {
        addPlaceBut.setOnAction(e -> {
            if(addQuantity!=null){
                addQuantity.run();
            }
        });

        delOneQuantityBut.setOnAction(e -> {
            if(delQuantity!=null){
                delQuantity.run();
            }
        });

        deletePlaceFromCartBut.setOnAction(e -> {
            if(delPlace!=null){
                delPlace.run();
            }
        });
    }


    public void setTexts(String name,String description,Float price,Integer quantity) {
        nameCartField.setText(name);
        descriptionCartField.setText(description);
        priceCartField.setText(String.valueOf(price));
        quantityCartField.setText(String.valueOf(quantity));
    }

    public void setItemPane(AnchorPane itemPane) {this.itemPane = itemPane;}
    public void setDelQuantity(Runnable delQuantity) {this.delQuantity = delQuantity;}
    public void setAddQuantity(Runnable addQuantity) {this.addQuantity = addQuantity;}
    public void setDelPlace(Runnable delPlace) {this.delPlace = delPlace;}

    public void disableAddButton() {
        addPlaceBut.setDisable(true);
        addPlaceBut.setStyle("-fx-background-color: grey");
    }
}
