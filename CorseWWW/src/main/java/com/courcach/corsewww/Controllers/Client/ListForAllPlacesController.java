package com.courcach.corsewww.Controllers.Client;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class ListForAllPlacesController {
    private AnchorPane itemPane;
    private Runnable thr;

    @FXML
    private Button addButton;

    @FXML
    private Label labelForCategory;

    @FXML
    private Label labelForDescription;

    @FXML
    private Label labelForName;

    @FXML
    private Label labelForPrice;

    @FXML
    private Label labelForQuantity;


    public void initialize() {
        addButton.setOnAction(e -> {
           if(thr!=null){
               thr.run();
           }
        });
    }

    public void setTexts(String name,String description,String category,Float price,Integer quantity) {
        labelForName.setText(name);
        labelForDescription.setText(description);
        labelForCategory.setText(category);
        labelForPrice.setText(String.valueOf(price));
        labelForQuantity.setText(String.valueOf(quantity));
    }

    public void setItemPane(AnchorPane itemPane) {this.itemPane = itemPane;}

    public void setThr(Runnable thr) {this.thr = thr;}
}
