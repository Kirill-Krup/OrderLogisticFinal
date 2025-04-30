package com.courcach.corsewww.Controllers.Client;

import com.courcach.Server.Services.Client.ClientRequest;
import com.courcach.corsewww.Models.ConnectionToServer;
import com.courcach.corsewww.Models.Model;
import com.courcach.corsewww.Views.NotificationUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ReplenishmnetOfWalletController {

    @FXML
    private Button ReplenishmnetOfWalletBut;

    @FXML
    private Button addNewOrder;

    @FXML
    private Button backBut;

    @FXML
    private TextField cardNumber;

    @FXML
    private TextField cardUser;

    @FXML
    private Button checkMyOrder;

    @FXML
    private Button checkUserHistory;

    @FXML
    private TextField cvvCode;

    @FXML
    private Button exitBut;

    @FXML
    private Button homeText;

    @FXML
    private Pane mainPane;

    @FXML
    private VBox menuBox;

    @FXML
    private Button menuBut;

    @FXML
    private Button openWallet;

    @FXML
    private TextField sum;

    @FXML
    private HBox topPane;

    @FXML
    private TextField validityPeriod;

    @FXML
    private Label MyWallet;

    @FXML
    private StackPane notificationPane;

    @FXML
    private Button reportsBut;


    public void initialize() {
        initial();
        ConnectionToServer connection = Model.getInstance().getConnectionToServer();
        ReplenishmnetOfWalletBut.setOnAction(event -> {
           if(cardNumber.getText().trim().isEmpty() || cardUser.getText().trim().isEmpty() || cvvCode.getText().trim().isEmpty() || validityPeriod.getText().trim().isEmpty() || sum.getText().trim().isEmpty()) {
              NotificationUtil.showErrorNotification(notificationPane,"Введены не все поля");
              return;
           }
           String CardNumber = cardNumber.getText();
           String CardUser = cardUser.getText();
           String CvvCode = cvvCode.getText();
           String ValidityPeriod = validityPeriod.getText();
           Float Sum = Float.parseFloat(sum.getText());
           connection.sendObject(new ClientRequest("replenishmentWallet",Model.getInstance().getCurrentUser(), Sum));
           Model.getInstance().getCurrentUser().setWallet(Model.getInstance().getCurrentUser().getWallet() + Sum);
           NotificationUtil.showNotification(notificationPane,"Кошелёк успешно пополнен");
        });
    }


    private void initial(){
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

        homeText.setOnAction(e -> {
            Stage stage = (Stage) homeText.getScene().getWindow();
            Model.getInstance().getViewFactory().closeStage(stage);
            Model.getInstance().getViewFactory().showClientWindow();
        });

        reportsBut.setOnAction(e -> {
            Stage stage = (Stage) reportsBut.getScene().getWindow();
            Model.getInstance().getViewFactory().closeStage(stage);
            Model.getInstance().getViewFactory().showReportsWindow();
        });

        exitBut.setOnAction(e -> {
            Model.getInstance().getConnectionToServer().sendObject(new ClientRequest("exit"));
            Stage stage = (Stage) menuBut.getScene().getWindow();
            Model.getInstance().getViewFactory().closeStage(stage);
            Model.getInstance().getViewFactory().showLoginWindow();
        });
    }
}
