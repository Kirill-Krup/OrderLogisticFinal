package com.courcach.corsewww.Controllers.Employee;

import com.courcach.Server.Services.ClassesForRequests.ModelForTable;
import com.courcach.Server.Services.ClassesForRequests.Orders;
import com.courcach.Server.Services.ClassesForRequests.Places;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;

import java.util.List;

public class LineOfOrderController {
    private Runnable add;
    private Runnable refusal;
    private AnchorPane itemPane;

    @FXML
    private Button acceptBut;

    @FXML
    private Label addressField;

    @FXML
    private TableColumn<Places, Integer> allQuantityColumn;

    @FXML
    private Label dateField;

    @FXML
    private Label loginField;

    @FXML
    private TableColumn<Orders, String> nameColumn;

    @FXML
    private Label priceField;

    @FXML
    private TableColumn<Orders, Integer> quantityColumn;

    @FXML
    private Button refusalBut;

    @FXML
    private TableView<ModelForTable> tableForPlaces;

    @FXML
    private Line lineStatus;

    @FXML
    private ImageView crestImg;

    @FXML
    private ImageView galkaImg;

    @FXML
    private Label orderNumberField;


    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("placeName"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        allQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("allQuantity"));
        acceptBut.setOnAction(event -> {
            if(add!=null){
                add.run();
            }
        });

        refusalBut.setOnAction(e -> {
           if(refusal!=null){
               refusal.run();
           }
        });
    }

    public void fillAll(int orderNumber,String login, Float totalPrice, String address, String date, Orders.OrderStatus status,
                        List<Places> orderedPlaces, List<Places> allPlaces) {
        loginField.setText(login);
        orderNumberField.setText("№"+orderNumber);
        addressField.setText(address);
        dateField.setText(date);
        priceField.setText(Float.toString(totalPrice));
        if(status == Orders.OrderStatus.НОВЫЙ){
            lineStatus.setStroke(Paint.valueOf("#449803"));
            acceptBut.setDisable(false);
            refusalBut.setDisable(false);
            crestImg.setVisible(true);
            galkaImg.setVisible(true);
        }else if(status == Orders.OrderStatus.В_ПРОЦЕССЕ){
            lineStatus.setStroke(Paint.valueOf("#025e6a"));
            acceptBut.setDisable(true);
            refusalBut.setDisable(true);
            crestImg.setVisible(false);
            galkaImg.setVisible(false);
        }else if(status == Orders.OrderStatus.ОТМЕНЁН){
            lineStatus.setStroke(Paint.valueOf("#9f0000"));
            acceptBut.setDisable(true);
            refusalBut.setDisable(true);
            crestImg.setVisible(false);
            galkaImg.setVisible(false);
        }else if(status == Orders.OrderStatus.ВЫПОЛНЕН){
            lineStatus.setStroke(Paint.valueOf("#0a4c00"));
            acceptBut.setDisable(true);
            refusalBut.setDisable(true);
            crestImg.setVisible(false);
            galkaImg.setVisible(false);
        } else if (status == Orders.OrderStatus.ПРИНЯТ) {
            lineStatus.setStroke(Paint.valueOf("#ef5902"));
            acceptBut.setDisable(true);
            refusalBut.setDisable(true);
            crestImg.setVisible(false);
            galkaImg.setVisible(false);
        }else if (status == Orders.OrderStatus.ОТКАЗАНО) {
            lineStatus.setStroke(Paint.valueOf(String.valueOf(Color.RED)));
            acceptBut.setDisable(true);
            refusalBut.setDisable(true);
            crestImg.setVisible(false);
            galkaImg.setVisible(false);
        }
        ObservableList<ModelForTable> tableData = FXCollections.observableArrayList();
        for (Places orderedPlace : orderedPlaces) {
            Places allPlace = allPlaces.stream()
                    .filter(p -> p.getPlaceName().equals(orderedPlace.getPlaceName()))
                    .findFirst()
                    .orElse(null);
            int allQty = allPlace != null ? allPlace.getQuantity() : 0;
            ModelForTable model = new ModelForTable(orderedPlace.getPlaceName(), orderedPlace.getQuantity(), allQty);
            tableData.add(model);
        }
        tableForPlaces.setItems(tableData);
    }
    public void setItemPane(AnchorPane itemPane) {this.itemPane = itemPane;}

    public void setAdd(Runnable add) {this.add = add;}
    public void setRefusal(Runnable refusal) {this.refusal = refusal;}
}
