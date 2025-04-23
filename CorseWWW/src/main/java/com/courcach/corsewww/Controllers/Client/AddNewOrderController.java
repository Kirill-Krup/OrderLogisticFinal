package com.courcach.corsewww.Controllers.Client;

import com.courcach.Server.Services.ClassesForRequests.Places;
import com.courcach.Server.Services.Client.ClientRequest;
import com.courcach.corsewww.Models.ConnectionToServer;
import com.courcach.corsewww.Models.Model;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javax.swing.text.html.ImageView;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class AddNewOrderController {
    private List<Places> allPlaces;
    private int count = 0;

    @FXML
    private Button deleteBut;

    @FXML
    private Button formOrder;

    @FXML
    private TextField adressTextField;

    @FXML
    private TextField fio;

    @FXML
    private TextField phone;

    @FXML
    private Text redStar;

    @FXML
    private Text errorLabel;

    @FXML
    private ComboBox<String> selectAdress;

    @FXML
    private ListView<AnchorPane> listOfPlaces;

    @FXML
    private ComboBox<String> selectTypeOfDelivery;

    @FXML
    private Button menuBut;

    @FXML
    private Button backBut;

    @FXML
    private Button exitBut;

    @FXML
    private Button addNewOrder;

    @FXML
    private Pane mainPane;

    @FXML
    private Pane topPane;

    @FXML
    private VBox menuBox;

    @FXML
    private Button homeText;

    @FXML
    private Button openWallet;

    @FXML
    private Button cart;

    @FXML
    private Label counter;

    public void initialize() {
        ObservableList<String> items = FXCollections.observableArrayList("Самовывоз", "Доставка");
        ObservableList<String> adresses = FXCollections.observableArrayList("ул. Ленина 10, ТЦ Столица, 1 этаж", "пр. Независимости 46, ТЦ Галерея, 2ой этаж", "ул. Притыцкого 156, ТЦ Тивали, 2 этаж", "ул. Кальварийская 6, ТЦ Экспобел", "ул. Янки Купалы 17, ТЦ Европа, -1 этаж");
        selectTypeOfDelivery.setItems(items);
        selectAdress.setItems(adresses);
        ConnectionToServer connect = Model.getInstance().getConnectionToServer();
        connect.sendObject(new ClientRequest("giveMeAllMaterials"));
        allPlaces = (List<Places>) connect.receiveObject();
        initial();
        refreshPlacesList();
        deleteBut.setOnAction(event -> {
           fio.setText("");
           phone.setText("");
        });

        selectTypeOfDelivery.setOnAction(event -> {
            if(Objects.equals(selectTypeOfDelivery.getValue(), "Доставка")) {
                adressTextField.setOpacity(1);
                adressTextField.setMouseTransparent(false);
                redStar.setOpacity(1);
                if(selectAdress.getOpacity() == 1) {
                    selectAdress.setOpacity(0);
                    selectAdress.setMouseTransparent(true);
                }
            }
            else if (Objects.equals(selectTypeOfDelivery.getValue(),"Самовывоз")){
                selectAdress.setOpacity(1);
                selectAdress.setMouseTransparent(false);
                redStar.setOpacity(1);
                if(adressTextField.getOpacity() == 1) {
                    adressTextField.setOpacity(0);
                    adressTextField.setMouseTransparent(true);
                }
            }
        });

        formOrder.setOnAction(event -> {
           String FIO = fio.getText();
           String PHONE = phone.getText();
            if(selectTypeOfDelivery.getValue() == "Доставка"){
                String ADRESS = adressTextField.getText();
            }else if(selectTypeOfDelivery.getValue() == "Самовывоз"){
                String ADRESS = selectAdress.getSelectionModel().getSelectedItem();
            }
            else{
                showError("Ошибка заказа, попробуйте позже!");
                return;
            }
            connect.sendObject(new ClientRequest("addNewOrder"));
        });
    }
    private void showError(String message){
        errorLabel.setText(message);
    }



    private void initial(){
        counter.setText(String.valueOf(count));
        menuBox.setMouseTransparent(true);
        menuBut.setOnAction(e -> {
            menuBox.setOpacity(1);
            topPane.setStyle("-fx-background-color: rgba(0,0,0,0.8)");
            mainPane.setStyle("-fx-background-color: rgba(0,0,0,0.8)");
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

    private void refreshPlacesList(){
        listOfPlaces.getItems().clear();
        for(Places place : allPlaces){
            addPlaces(place);
        }
    }

    private void addPlaces(Places place) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Fxml/Client/ListForAddPlaces.fxml"));
        AnchorPane cellForPlace;
        try {
            cellForPlace = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ListForAllPlacesController controller = fxmlLoader.getController();
        controller.setTexts(place.getPlaceName(),place.getDescription(),place.getCategory(),place.getPrice(),place.getQuantity());
        controller.setItemPane(cellForPlace);
        controller.setThr(()->{
            count++;
            counter.setText(String.valueOf(count));
        });
        listOfPlaces.getItems().add(cellForPlace);
    }

}
