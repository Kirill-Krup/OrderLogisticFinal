package com.courcach.corsewww.Controllers.Client;

import com.courcach.Server.Services.Client.ClientRequest;
import com.courcach.corsewww.Models.ConnectionToServer;
import com.courcach.corsewww.Models.Model;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ReplenishmnetOfWalletController {

    @FXML
    private Button ReplenishmnetOfWalletBut;

    @FXML
    private TextField cardNumber;

    @FXML
    private TextField cardUser;

    @FXML
    private TextField cvvCode;

    @FXML
    private Button exitBut;

    @FXML
    private TextField validityPeriod;

    @FXML
    private Text errorLabel;

    @FXML
    private TextField sum;


    public void initialize() {
        ConnectionToServer connection = Model.getInstance().getConnectionToServer();
        exitBut.setOnAction(event -> {
            Stage stage = (Stage) exitBut.getScene().getWindow();
            Model.getInstance().getViewFactory().closeStage(stage);
            Model.getInstance().getViewFactory().showClientWindow();
        });

        ReplenishmnetOfWalletBut.setOnAction(event -> {
           if(cardNumber.getText().trim().isEmpty() || cardUser.getText().trim().isEmpty() || cvvCode.getText().trim().isEmpty() || validityPeriod.getText().trim().isEmpty() || sum.getText().trim().isEmpty()) {
              errorLabel.setText("Введены не все поля");
              return;
           }
           String CardNumber = cardNumber.getText();
           String CardUser = cardUser.getText();
           String CvvCode = cvvCode.getText();
           String ValidityPeriod = validityPeriod.getText();
           Float Sum = Float.parseFloat(sum.getText());
           connection.sendObject(new ClientRequest("replenishmentWallet",Sum));
        });
    }

}
