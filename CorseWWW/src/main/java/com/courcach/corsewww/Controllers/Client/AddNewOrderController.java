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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AddNewOrderController {
    Orders newOrder = new Orders();
    private List<Places> allPlaces;
    private List<Places> myOrders;
    private int count = 0;
    private static Boolean cartIsOpen = false;
    private static float resulPrice= 0;

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

    @FXML
    private ComboBox<String>typeOfPaintment;

    @FXML
    private ListView<AnchorPane> cartList;

    @FXML
    private Pane paneForBlackCart;

    @FXML
    private Label labelForResultSum;

    @FXML
    private StackPane notificationPane;

    @FXML
    private Label BYNText;

    @FXML
    private Label MyWallet;

    public void initialize() {
        initialLists();
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
            if(fio.getText().isEmpty()) {
                NotificationUtil.showErrorNotification( notificationPane,"Введите ФИО");
                return;
            }else{
                newOrder.setFIO(fio.getText());
            }
            if(phone.getText().isEmpty()) {
                NotificationUtil.showErrorNotification( notificationPane,"Введите телефон");
                return;
            }else{
                newOrder.setPhone(phone.getText());
            }
            if (typeOfPaintment.getValue().isEmpty()) {
                NotificationUtil.showErrorNotification( notificationPane,"Выберите тип оплаты");
                return;
            }else{
                newOrder.setTypeOfPayment(typeOfPaintment.getValue());
            }
            if(selectTypeOfDelivery.getValue().equals("Доставка")){
                newOrder.setTypeOfDelivery(selectTypeOfDelivery.getValue());
                newOrder.setAddressOfDelivery(adressTextField.getText());
            }else if(selectTypeOfDelivery.getValue().equals("Самовывоз")){
                newOrder.setTypeOfDelivery(selectTypeOfDelivery.getValue());
                newOrder.setAddressOfDelivery(selectAdress.getSelectionModel().getSelectedItem());
            }
            else{
                NotificationUtil.showErrorNotification( notificationPane,"Выберите место доставки");
                return;
            }
            if(typeOfPaintment.getValue().equals("Онлайн")){
                if(Model.getInstance().getCurrentUser().getWallet()<resulPrice){
                    NotificationUtil.showErrorNotification( notificationPane,"На вашем балансе недостаточно средств!");
                    return;
                }else{
                    Model.getInstance().getCurrentUser().setWallet(Model.getInstance().getCurrentUser().getWallet()-resulPrice);
                }
            }
            newOrder.setUserLogin(Model.getInstance().getCurrentUser().getLogin());
            newOrder.setTotalPrice(resulPrice);
            newOrder.setOrderPlaces(myOrders);
            connect.sendObject(new ClientRequest("addNewOrder",newOrder));
            NotificationUtil.showNotification(notificationPane,"Заказ успешно создан");
        });


        cart.setOnAction(event -> {
            if (cartIsOpen) {
                cartIsOpen = false;
                cartList.setStyle("-fx-background-color: transparent; -fx-opacity: 0;");
                cartList.setMouseTransparent(true);
                paneForBlackCart.setStyle("-fx-background-color: transparent; -fx-opacity: 0;");
                paneForBlackCart.setMouseTransparent(true);
                topPane.setStyle("-fx-background-color: rgba(30,29,29,0.8)");
                exitBut.setDisable(false);
                labelForResultSum.setStyle("-fx-text-fill: white;");
                BYNText.setStyle("-fx-text-fill: white;");
                cartList.toBack();
                paneForBlackCart.toBack();
            } else {
                refreshCartOpener();
            }
        });


    }

    private void initialLists(){
        myOrders = new ArrayList<>();
        ObservableList<String> items = FXCollections.observableArrayList("Самовывоз", "Доставка");
        ObservableList<String> addresses = FXCollections.observableArrayList("ул. Ленина 10, ТЦ Столица, 1 этаж", "пр. Независимости 46, ТЦ Галерея, 2ой этаж", "ул. Притыцкого 156, ТЦ Тивали, 2 этаж", "ул. Кальварийская 6, ТЦ Экспобел", "ул. Янки Купалы 17, ТЦ Европа, -1 этаж");
        ObservableList<String> paymentTypes = FXCollections.observableArrayList("При получении","Онлайн");
        selectTypeOfDelivery.setItems(items);
        typeOfPaintment.setItems(paymentTypes);
        selectAdress.setItems(addresses);
    }

    private void initial(){
        counter.setText(String.valueOf(count));
        menuBox.setMouseTransparent(true);
        menuBut.setOnAction(e -> {
            MyWallet.setText(String.valueOf(Model.getInstance().getCurrentUser().getWallet()));
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
        List<Places> available = new ArrayList<>();
        List<Places> outOfStock = new ArrayList<>();

        for (Places place : allPlaces) {
            if (place.getQuantity() > 0) {
                available.add(place);
            } else {
                outOfStock.add(place);
            }
        }
        for (Places place : available) {
            addPlaces(place, false);
        }
        for (Places place : outOfStock) {
            addPlaces(place, true);
        }
    }

    private void addPlaces(Places place, boolean isOutOfStock) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Fxml/Client/ListForAddPlaces.fxml"));
        AnchorPane cellForPlace;
        try {
            cellForPlace = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ListForAllPlacesController controller = fxmlLoader.getController();
        controller.setTexts(place.getPlaceName(), place.getDescription(), place.getCategory(), place.getPrice(), place.getQuantity());
        controller.setItemPane(cellForPlace);
        if (isOutOfStock) {
            cellForPlace.setStyle("-fx-background-color: #ffe6e6;");
            controller.disableAddButton();
        } else {
            controller.setThr(() -> {
                if (place.getQuantity() > 0) {
                    myOrders.add(new Places(place.getPlaceName(), place.getDescription(), place.getPrice(), place.getCategory(), 1));
                    refreshSum();
                    labelForResultSum.setText(String.valueOf(resulPrice));
                    refreshCounter("+");
                    place.setQuantity(place.getQuantity() - 1);
                    refreshPlacesList();
                }
            });
        }
        listOfPlaces.getItems().add(cellForPlace);
    }

    private void refreshSum() {
        resulPrice = 0;
        for(Places order:myOrders){
            resulPrice = resulPrice + order.getQuantity()*order.getPrice();
        }
        labelForResultSum.setText(String.valueOf(resulPrice));
    }

    private void refreshCartItems(){
        cartList.getItems().clear();
        for(Places place : myOrders){
            addCartItem(place);
        }
    }

    private void addCartItem(Places place) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Fxml/Client/CartItem.fxml"));
        AnchorPane cellForCart;
        try {
            cellForCart = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        CartItemController controller = fxmlLoader.getController();
        controller.setTexts(place.getPlaceName(),place.getDescription(),place.getPrice(),place.getQuantity());
        controller.setItemPane(cellForCart);
        for (Places originalPlace : allPlaces) {
            if (originalPlace.getPlaceName().equals(place.getPlaceName()) && originalPlace.getQuantity() <= 0) {
                controller.disableAddButton();
                break;
            }
        }
        controller.setDelQuantity(() -> {
            for (Places originalPlace : allPlaces) {
                if (originalPlace.getPlaceName().equals(place.getPlaceName())) {
                    originalPlace.setQuantity(originalPlace.getQuantity() + 1);
                    break;
                }
            }
            if (place.getQuantity() == 1) {
                myOrders.remove(place);
                refreshCartOpener();

            } else {
                place.setQuantity(place.getQuantity() - 1);
            }
            refreshCounter("-");
            refreshSum();
            refreshCartItems();
            refreshPlacesList();
            if (myOrders.isEmpty()) {
                refreshCartOpener();
                refreshSum();
            }
        });
        controller.setDelPlace(() -> {
            for (Places originalPlace : allPlaces) {
                if (originalPlace.getPlaceName().equals(place.getPlaceName())) {
                    originalPlace.setQuantity(originalPlace.getQuantity() + place.getQuantity());
                    break;
                }
            }
            myOrders.remove(place);
            refreshCounter("-");
            refreshCartItems();
            refreshPlacesList();
            refreshSum();
            if (myOrders.isEmpty()) {
                refreshCartOpener();
                refreshCounter("0");
                refreshSum();
            }
        });
        controller.setAddQuantity(() -> {
            for (Places originalPlace : allPlaces) {
                if (originalPlace.getPlaceName().equals(place.getPlaceName())) {
                    if (originalPlace.getQuantity() > 0) {
                        place.setQuantity(place.getQuantity() + 1);
                        originalPlace.setQuantity(originalPlace.getQuantity() - 1); 
                        resulPrice += place.getPrice();
                        labelForResultSum.setText(String.valueOf(resulPrice));
                        refreshCartItems();
                        refreshCounter("+");
                    } else {
                        controller.disableAddButton();
                    }
                    break;
                }
            }
            refreshSum();
        });
        cartList.getItems().add(cellForCart);
    }

    private void refreshCartOpener() {
        if (myOrders.isEmpty()) {
            cartIsOpen = false;
            cartList.setStyle("-fx-background-color: transparent; -fx-opacity: 0;");
            cartList.setMouseTransparent(true);
            paneForBlackCart.setStyle("-fx-background-color: transparent; -fx-opacity: 0;");
            paneForBlackCart.setMouseTransparent(true);
            topPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");
            labelForResultSum.setStyle("-fx-text-fill: white;");
            BYNText.setStyle("-fx-text-fill: white;");
            cartList.toBack();
            paneForBlackCart.toBack();
            exitBut.setDisable(false);
            return;
        }
        if (!cartIsOpen) {
            cartIsOpen = true;
            refreshCartItems();
            labelForResultSum.setStyle("-fx-text-fill: black;");
            BYNText.setStyle("-fx-text-fill: black;");
            cartList.setStyle("-fx-background-color: white; -fx-opacity: 1;");
            cartList.setMouseTransparent(false);
            paneForBlackCart.setStyle("-fx-background-color: rgba(0,0,0,0.8); -fx-opacity: 1;");
            paneForBlackCart.setMouseTransparent(false);
            topPane.setStyle("-fx-background-color: #fff");
            exitBut.setDisable(true);
            cartList.toFront();
            paneForBlackCart.toFront();
        }
    }


    private void refreshCounter(String operation){
        if(operation.equals("-")){
            count--;
            counter.setText(String.valueOf(count));
        }else if(operation.equals("+")){
            count++;
            counter.setText(String.valueOf(count));
        }else if(operation.equals("0")){
            count=0;
            counter.setText(String.valueOf(count));
        }
    }
}