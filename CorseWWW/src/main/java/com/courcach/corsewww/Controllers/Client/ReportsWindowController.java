package com.courcach.corsewww.Controllers.Client;

import com.courcach.Server.Services.ClassesForRequests.Orders;
import com.courcach.Server.Services.ClassesForRequests.Places;
import com.courcach.Server.Services.ClassesForRequests.ReportModel;
import com.courcach.Server.Services.Client.ClientRequest;
import com.courcach.corsewww.Models.ConnectionToServer;
import com.courcach.corsewww.Models.Model;
import com.courcach.corsewww.Views.NotificationUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

public class ReportsWindowController {
    private ConnectionToServer connection = Model.getInstance().getConnectionToServer();
    private List<Orders> allEndedOrders;
    private List<ReportModel> allSuitReports;
    private int counter = 0;
    int rating = 0;

    @FXML
    private Label MyWallet;

    @FXML
    private Button addNewOrder;

    @FXML
    private Button answersBut;

    @FXML
    private TextArea areaForReport;

    @FXML
    private Button backBut;

    @FXML
    private Button checkMyOrder;

    @FXML
    private Button checkUserHistory;

    @FXML
    private Label counterLabel;

    @FXML
    private Button exitBut;

    @FXML
    private Button mainBut;

    @FXML
    private ListView<AnchorPane> listForComments;

    @FXML
    private Pane mainPane;

    @FXML
    private VBox menuBox;

    @FXML
    private Button menuBut;

    @FXML
    private Button myReportsBut;

    @FXML
    private Button newReportBut;

    @FXML
    private StackPane notificationPane;

    @FXML
    private Button openWallet;

    @FXML
    private Pane paneWithRating;

    @FXML
    private Pane paneWithReport;

    @FXML
    private Button reportsBut;

    @FXML
    private Label noteText;

    @FXML
    private TextField searchField;

    @FXML
    private ImageView sadImage;

    @FXML
    private Button sendReportBut;

    @FXML
    private ToggleGroup starsGroup;

    @FXML
    private HBox topPane;




    public void initialize() {
        initialMenu();
        connection.sendObject(new ClientRequest("giveMeEndedOrdersAndReports",Model.getInstance().getCurrentUser()));   // Параметры статуса: ВЫПОЛНЕН
        allEndedOrders = (List<Orders>) connection.receiveObject();
        allSuitReports = (List<ReportModel>) connection.receiveObject();
        refreshCounter();
        refreshList();

        sendReportBut.setOnAction(event -> {
            int selectedIndex = listForComments.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0 && rating != 0 ) {
                Timestamp currentTime = new Timestamp(System.currentTimeMillis());
                AnchorPane selectedPane = listForComments.getSelectionModel().getSelectedItem();
                HistoryRowController controller = (HistoryRowController) selectedPane.getProperties().get("controller");
                Orders selectedOrder = controller.getOrder();
                ReportModel report = new ReportModel(currentTime,areaForReport.getText(),Model.getInstance().getCurrentUser().getLogin(),selectedOrder.getOrderNumber(),rating);
                connection.sendObject(new ClientRequest("newReport", report));
                String request = connection.receiveObject().toString();
                if(request.contains("успешно")){
                    NotificationUtil.showNotification(notificationPane, request);
                    refreshList();
                } else{NotificationUtil.showErrorNotification(notificationPane, request);}
            } else if(rating == 0) {
                NotificationUtil.showErrorNotification(notificationPane,"Вы не указали рейтинг");
            }
            else{
                NotificationUtil.showErrorNotification(notificationPane, "Вы не выбрали ни одного заказа");
            }
        });

        newReportBut.setOnAction(event -> { // Кнопка для отправки нового отзыва (ЛЕВО)
            paneWithRating.setVisible(true);
            sendReportBut.setVisible(true);
            areaForReport.setText("");
            areaForReport.setPromptText("Выберите заказ, на который хотите отправить отзыв");
            newReportBut.setStyle("-fx-background-color: green;-fx-border-radius: 10px 0px 0px 10px;-fx-background-radius: 10px 0px 0px 10px");
            myReportsBut.setStyle("-fx-background-color: transparent;");
            answersBut.setStyle("-fx-background-color: transparent;");
            searchField.setText("");
            refreshList();
        });

        myReportsBut.setOnAction(event -> {    // Кнопка для просмотра моих отзывов (ЦЕНТР)
            paneWithRating.setVisible(false);
            sendReportBut.setVisible(false);
            areaForReport.setText("");
            areaForReport.setPromptText("Выберите заказ, чтобы посмотреть информацию о нём");
            myReportsBut.setStyle("-fx-background-color: green;");
            answersBut.setStyle("-fx-background-color: transparent;");
            newReportBut.setStyle("-fx-background-color: transparent;");
            searchField.setText("");
            refreshListForMyReports();
        });

        answersBut.setOnAction(event -> {   // Кнопка для просмотра ответов админа (ПРАВО)
            paneWithRating.setVisible(false);
            sendReportBut.setVisible(false);
            areaForReport.setText("");
            areaForReport.setPromptText("Выберите заказ, чтобы посмотреть информацию о нём");
            answersBut.setStyle("-fx-background-color: green; -fx-border-radius: 0px 10px 10px 0px;-fx-background-radius: 0px 10px 10px 0px;");
            newReportBut.setStyle("-fx-background-color: transparent;");
            myReportsBut.setStyle("-fx-background-color: transparent;");
            connection.sendObject(new ClientRequest("checkAnswers", Model.getInstance().getCurrentUser()));
            allSuitReports = (List<ReportModel>) connection.receiveObject();
            searchField.setText("");
            refreshCounter();
            refreshlistForMyAnswers();
        });

        starsGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle != null) {
                rating = Integer.parseInt(newToggle.getUserData().toString());
                updateStarColors(rating);
            }
        });

        setupSettingForChoice();

        searchField.textProperty().addListener((obs, oldValue, newValue) -> {
           updateSearchResults(newValue.trim().toLowerCase());
        });

    }

    private void updateSearchResults(String searchQuery) {
        listForComments.getItems().clear();

        if (newReportBut.getStyle().contains("green")) {
            for (Orders order : allEndedOrders) {
                boolean hasReport = allSuitReports.stream()
                        .anyMatch(report -> report.getOrderNumber() == order.getOrderNumber());
                if (!hasReport && orderMatchesSearch(order, searchQuery)) {
                    addItem(order);
                }
            }
            toggleSadImage("Нет заказов на которые можно оставить отзыв");

        } else if (myReportsBut.getStyle().contains("green")) {
            for (ReportModel report : allSuitReports) {
                if (reportMatchesSearch(report, searchQuery)) {
                    addMyReports(report);
                }
            }
            toggleSadImage("Соответствий не найдено");

        } else if (answersBut.getStyle().contains("green")) {
            for (ReportModel report : allSuitReports) {
                if (report.getReportAnswer() != null && reportMatchesSearch(report, searchQuery)) {
                    addMyAnswers(report);
                }
            }
            toggleSadImage("Соответствий не найдено");
        }
    }


    private boolean orderMatchesSearch(Orders order, String query) {
        if (query.isEmpty()) return true;
        return String.valueOf(order.getOrderNumber()).contains(query);
    }

    private boolean reportMatchesSearch(ReportModel report, String query) {
        if (query.isEmpty()) return true;
        return String.valueOf(report.getOrder().getOrderNumber()).contains(query);
    }

    private void toggleSadImage(String message) {
        if (listForComments.getItems().isEmpty()) {
            sadImage.setVisible(true);
            noteText.setText(message);
            noteText.setVisible(true);
        } else {
            sadImage.setVisible(false);
            noteText.setVisible(false);
        }
    }

    private void refreshlistForMyAnswers() {
        sadImage.setVisible(false);
        noteText.setVisible(false);
        listForComments.getItems().clear();
        for(ReportModel report : allSuitReports) {
            if(report.getReportAnswer()!=null) {
                addMyAnswers(report);
            }
        }
        if(listForComments.getItems().isEmpty()){
            sadImage.setVisible(true);
            noteText.setText("На ваши отзывы ещё не было ответов");
            noteText.setVisible(true);
        }
    }

    private void setupSettingForChoice() {
        listForComments.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                Object controllerObj = newValue.getProperties().get("controller");
                if(controllerObj instanceof MyReportRowController reportRowController){
                    ReportModel report = (ReportModel) reportRowController.getReportModel();
                    areaForReport.setText("Ваш заказ: ");
                    for(Places place : report.getOrder().getOrderPlaces()){
                        areaForReport.setText(areaForReport.getText()+"\n"+ "Наименование товара: " + place.getPlaceName() + "\n" + "Цена: " + place.getPrice());
                    }
                }
            }
        });


    }

    private void updateStarColors(int rating) {
        for (Toggle toggle : starsGroup.getToggles()) {
            ToggleButton button = (ToggleButton) toggle;
            int value = Integer.parseInt(button.getUserData().toString());
            button.setTextFill(value <= rating ? Color.GOLD : Color.WHITE);
        }
    }

    private void refreshList(){
        sadImage.setVisible(false);
        noteText.setVisible(false);
        listForComments.getItems().clear();
        for(Orders order : allEndedOrders){
            boolean hasReport = false;
            for(ReportModel report : allSuitReports){
                if(order.getOrderNumber() == report.getOrderNumber()){
                    hasReport = true;
                    break;
                }
            }
            if(!hasReport){
                addItem(order);
            }

        }
        if(listForComments.getItems().isEmpty()){
            sadImage.setVisible(true);
            noteText.setText("Нет заказов на которые можно оставить отзыв");
            noteText.setVisible(true);
        }
    }

    private void addItem(Orders order){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Fxml/Client/HistoryRow.fxml"));
        AnchorPane cellForOrder;
        try {
            cellForOrder = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        HistoryRowController controller = fxmlLoader.getController();
        controller.setTexts(order);
        controller.setItemPane(cellForOrder);
        cellForOrder.getProperties().put("controller", controller);
        listForComments.getItems().add(cellForOrder);
    }

    private void refreshCounter(){
        counter = 0;
        for(ReportModel report : allSuitReports){
            if(report.getReportAnswer() != null && !report.getChecked()){
                counter++;
            }
        }
        counterLabel.setText(String.valueOf(counter));
    }


    private void refreshListForMyReports(){
        sadImage.setVisible(false);
        noteText.setVisible(false);
        listForComments.getItems().clear();
        for(ReportModel report : allSuitReports){
            addMyReports(report);
        }
        if(listForComments.getItems().isEmpty()){
            sadImage.setVisible(true);
            noteText.setText("Вы ещё не оставляли отзывы");
            noteText.setVisible(true);
        }
    }

    private void addMyReports(ReportModel report){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Fxml/Client/myReportRow.fxml"));
        AnchorPane cellForOrder;
        try {
            cellForOrder = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        MyReportRowController controller = fxmlLoader.getController();
        controller.setTexts(report);
        controller.setItemPane(cellForOrder);
        cellForOrder.getProperties().put("controller", controller);
        listForComments.getItems().add(cellForOrder);
    }

    private void addMyAnswers(ReportModel report){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Fxml/Client/myReportRow.fxml"));
        AnchorPane cellForOrder;
        try {
            cellForOrder = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        MyReportRowController controller = fxmlLoader.getController();
        controller.setText(report);
        controller.setItemPane(cellForOrder);
        cellForOrder.getProperties().put("controller", controller);
        listForComments.getItems().add(cellForOrder);
    }


    private void initialMenu(){
        menuBox.setMouseTransparent(true);
        MyWallet.setText(String.valueOf(Model.getInstance().getCurrentUser().getWallet()));
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

        checkMyOrder.setOnAction(event->{
            Stage stage = (Stage) checkMyOrder.getScene().getWindow();
            Model.getInstance().getViewFactory().closeStage(stage);
            Model.getInstance().getViewFactory().showCheckMyOrderWindow();
        });

        checkUserHistory.setOnAction(e -> {
            Stage stage = (Stage) checkUserHistory.getScene().getWindow();
            Model.getInstance().getViewFactory().closeStage(stage);
            Model.getInstance().getViewFactory().showCheckUserHistoryWindow();
        });

        openWallet.setOnAction(e -> {
            Stage stage = (Stage) openWallet.getScene().getWindow();
            Model.getInstance().getViewFactory().closeStage(stage);
            Model.getInstance().getViewFactory().showWalletWindow();
        });

        reportsBut.setOnAction(e -> {
            Stage stage = (Stage) reportsBut.getScene().getWindow();
            Model.getInstance().getViewFactory().closeStage(stage);
            Model.getInstance().getViewFactory().showReportsWindow();
        });

        mainBut.setOnAction(e -> {
            Stage stage = (Stage) mainBut.getScene().getWindow();
            Model.getInstance().getViewFactory().closeStage(stage);
            Model.getInstance().getViewFactory().showClientWindow();
        });

        backBut.setOnAction(e -> {
            menuBox.setOpacity(0);
            menuBox.setMouseTransparent(true);
            topPane.setStyle("-fx-background-color: rgba(0,0,0,0.5)");
            mainPane.setStyle("-fx-background-color: transparent");
        });


        exitBut.setOnAction(e -> {
            Model.getInstance().getConnectionToServer().sendObject(new ClientRequest("exit"));
            Stage stage = (Stage) menuBut.getScene().getWindow();
            Model.getInstance().getViewFactory().closeStage(stage);
            Model.getInstance().getViewFactory().showLoginWindow();
        });
    }
}

