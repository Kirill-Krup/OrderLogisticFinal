package com.courcach.corsewww.Controllers.User;

import com.courcach.Server.Services.User.ForgotPassword.ForgotPasswordRequest;
import com.courcach.Server.Services.User.ForgotPassword.ForgotPasswordResponce;
import com.courcach.corsewww.Models.ConnectionToServer;
import com.courcach.corsewww.Models.Model;
import com.courcach.corsewww.Views.NotificationUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class ForgotPasswordController {

    @FXML
    private Button cancelButton;

    @FXML
    private TextField emailField;

    @FXML
    private Button recoverButton;

    @FXML
    private StackPane notificationPane;


    public void initialize() {
        ConnectionToServer connection = Model.getInstance().getConnectionToServer();
        cancelButton.setOnAction(action -> {
           Stage stage = (Stage) cancelButton.getScene().getWindow();
            Model.getInstance().getViewFactory().closeStage(stage);
            Model.getInstance().getViewFactory().showLoginWindow();
        });

        recoverButton.setOnAction(action -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/User/DialogForCaptcha.fxml"));
                DialogPane dialogPane = loader.load();
                DialogForCaptchaController controllerForCaptcha = loader.getController();
                Dialog<Void> dialog = new Dialog<>();
                dialog.setTitle("Captcha");
                dialog.setDialogPane(dialogPane);
                controllerForCaptcha.setDialog(dialog);
                dialog.showAndWait();
                if(controllerForCaptcha.getIsCorrectAnswer()){
                    String mail = emailField.getText();
                    if (!mail.contains("@") || !mail.contains(".") || mail.length() < 5) {
                        NotificationUtil.showErrorNotification(notificationPane,"Формат почты не верен");
                        return;
                    }
                    connection.sendObject(new ForgotPasswordRequest("forgotPassword", mail));
                    ForgotPasswordResponce responce = (ForgotPasswordResponce) connection.receiveObject();
                    if(responce.getResponce()){
                        Stage stage = (Stage) cancelButton.getScene().getWindow();
                        Model.getInstance().getViewFactory().closeStage(stage);
                        Model.getInstance().getViewFactory().showMailCodeWindow();
                    }else{
                        NotificationUtil.showErrorNotification(notificationPane,responce.getMessage());
                    }
                }else{
                    NotificationUtil.showErrorNotification(notificationPane,"Вы не прошли каптчу");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

}

