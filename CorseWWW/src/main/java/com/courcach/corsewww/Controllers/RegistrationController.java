package com.courcach.corsewww.Controllers;

import com.courcach.Server.Services.AuthRequest;
import com.courcach.Server.Services.ClassesForRequests.Users;
import com.courcach.Server.Services.RegRequest;
import com.courcach.Server.Services.RegResponse;
import com.courcach.corsewww.Models.ConnectionToServer;
import com.courcach.corsewww.Models.Model;
import com.courcach.corsewww.Views.NotificationUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Timestamp;

public class RegistrationController {

    @FXML
    private TextField emailField;

    @FXML
    private TextField loginField;

    @FXML
    private TextField nameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField surnameField;

    @FXML
    private StackPane notificationPane;

    @FXML
    private Button registrationBut;

    @FXML
    private Button signInBut;



    public void initialize() {
        signInBut.setOnAction(e -> {
            Stage stage = (Stage) signInBut.getScene().getWindow();
            Model.getInstance().getViewFactory().closeStage(stage);
            Model.getInstance().getViewFactory().showLoginWindow();
        });

        registrationBut.setOnAction(e -> {
            String email = emailField.getText();
            String name = nameField.getText();
            String surname = surnameField.getText();
            String login = loginField.getText();
            String password = passwordField.getText();
            if(email.isEmpty() || name.isEmpty() || surname.isEmpty() || login.isEmpty() || password.isEmpty()) {
                NotificationUtil.showErrorNotification(notificationPane,"Заполнены не все поля");
                return;
            }
            if (!email.contains("@") || !email.contains(".") || email.length() < 5) {
                NotificationUtil.showErrorNotification(notificationPane,"Формат почты не верен");
                return;
            }
            if (name.length() <2 || surname.length()<2 || name.length()>45 || surname.length()>45) {
                NotificationUtil.showErrorNotification(notificationPane,"Имя или фамилия введены неверно");
                return;
            }
            if (login.length() < 5 || login.length() > 20) {
                NotificationUtil.showErrorNotification(notificationPane,"Логин должен содержать 5-20 символов");
                return;
            }
            if(password.length() < 8 || password.length() > 20) {
                NotificationUtil.showErrorNotification(notificationPane,"Пароль не может быть меня 8 символов");
                return;
            }
            ConnectionToServer connection = Model.getInstance().getConnectionToServer();
            connection.sendObject(new RegRequest(email,name,surname,login,password,"REGISTRATION"));
            RegResponse regResponse = (RegResponse) connection.receiveObject();
            System.out.println(regResponse.getMessage());
            if(regResponse.getMessage().contains("успешна")) {
                Timestamp currentTime = new Timestamp(System.currentTimeMillis());
                Model.getInstance().setCurrentUser(new Users(login,name,surname,email,0.0,currentTime));
                Stage stage = (Stage) registrationBut.getScene().getWindow();
                Model.getInstance().getViewFactory().closeStage(stage);
                Model.getInstance().getViewFactory().showClientWindow();
            }
            else if(regResponse.getMessage().contains("Логин")){
                NotificationUtil.showErrorNotification(notificationPane,"Такой логин уже используется");
            }else if(regResponse.getMessage().contains("Email")){
                NotificationUtil.showErrorNotification(notificationPane,"Такая почта уже используется");
            }else{
                NotificationUtil.showErrorNotification(notificationPane,"Ошибка регистрации, попробуйте позже");
            }
        });
    }

}

