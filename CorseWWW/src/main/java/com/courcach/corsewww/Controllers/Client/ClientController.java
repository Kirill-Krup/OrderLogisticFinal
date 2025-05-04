package com.courcach.corsewww.Controllers.Client;


import com.courcach.Server.Services.Client.ClientRequest;
import com.courcach.corsewww.Models.ConnectionToServer;
import com.courcach.corsewww.Models.Model;
import com.courcach.corsewww.Views.NotificationUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ClientController {
    @FXML
    private Button menuBut;

    @FXML
    private Button aboutUsBut;

    @FXML
    private Button backFromAboutUsBut;

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
    private Pane aboutUsPane;

    @FXML
    private VBox menuBox;

    @FXML
    private Text homeText;

    @FXML
    private Button openWallet;

    @FXML
    private Button checkMyOrder;

    @FXML
    private Label MyWallet;

    @FXML
    private Button checkUserHistory;

    @FXML
    private Button reportsBut;

    @FXML
    private StackPane notificationPane;

    public void initialize() {
        ConnectionToServer connect = Model.getInstance().getConnectionToServer();
        connect.sendObject(new ClientRequest("giveMeNewMessages",Model.getInstance().getCurrentUser()));
        boolean check = (Boolean) connect.receiveObject();
        if(check){
            NotificationUtil.showNotification(notificationPane,"У вас есть новые непрочитанные сообщения");
        }
        menuBox.setMouseTransparent(true);
        MyWallet.setText(String.valueOf(Model.getInstance().getCurrentUser().getWallet()));
        menuBut.setOnAction(e -> {
           menuBox.setOpacity(1);
           topPane.setStyle("-fx-background-color: rgba(0,0,0,0.8)");
           mainPane.setStyle("-fx-background-color: rgba(0,0,0,0.8)");
           menuBox.setMouseTransparent(false);
           if(aboutUsPane.getOpacity() == 1){
               aboutUsPane.setOpacity(0);
           }
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

        backBut.setOnAction(e -> {
            menuBox.setOpacity(0);
            menuBox.setMouseTransparent(true);
            topPane.setStyle("-fx-background-color: rgba(0,0,0,0.5)");
            mainPane.setStyle("-fx-background-color: rgba(0,0,0,0.5)");
        });

        aboutUsBut.setOnAction(e -> {
            aboutUsPane.setOpacity(1);
            aboutUsBut.setUnderline(true);
            homeText.setUnderline(false);
        });

        backFromAboutUsBut.setOnAction(e -> {
           aboutUsPane.setOpacity(0);
            aboutUsBut.setUnderline(false);
            homeText.setUnderline(true);
        });

        exitBut.setOnAction(e -> {
            Model.getInstance().getConnectionToServer().sendObject(new ClientRequest("exit"));
            Stage stage = (Stage) menuBut.getScene().getWindow();
            Model.getInstance().getViewFactory().closeStage(stage);
            Model.getInstance().getViewFactory().showLoginWindow();
        });
    }
}
