package com.courcach.corsewww.Controllers;

import com.courcach.Server.Services.User.Auth.AuthRequest;
import com.courcach.Server.Services.User.Auth.AuthResponse;
import com.courcach.Server.Services.ClassesForRequests.Users;
import com.courcach.corsewww.Models.ConnectionToServer;
import com.courcach.corsewww.Models.Model;
import com.courcach.corsewww.Views.NotificationUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private Button signUpTab;

    @FXML
    private Button enterBut;


    @FXML
    private TextField loginInput;

    @FXML
    private TextField passwordInput;

    @FXML
    private StackPane notificationPane;

    @FXML
    private Button forgotPasswordBut;


    public void initialize() {
        signUpTab.setOnAction(e -> {
            Stage stage = (Stage) signUpTab.getScene().getWindow();
            Model.getInstance().getViewFactory().closeStage(stage);
            Model.getInstance().getViewFactory().showRegistrationWindow();
        });

        forgotPasswordBut.setOnAction(e -> {
            Stage stage = (Stage) forgotPasswordBut.getScene().getWindow();
            Model.getInstance().getViewFactory().closeStage(stage);
            Model.getInstance().getViewFactory().showForgotPasswordWindow();
        });

        enterBut.setOnAction(e -> {
            String login = loginInput.getText();
            String password = passwordInput.getText();
            if(login.isEmpty() || password.isEmpty()) {
                NotificationUtil.showErrorNotification(notificationPane,"Введены не все поля");
                return;
            }
            ConnectionToServer connection = Model.getInstance().getConnectionToServer();
            connection.sendObject(new AuthRequest(login,password,"LOGIN"));
            AuthResponse response = (AuthResponse) connection.receiveObject();
            if(response.getMessage().contains("Неверно")) {
                NotificationUtil.showErrorNotification(notificationPane,"Неправильный логин или пароль");
                return;
            }
            if (response.getMessage().contains("заблокирован")) {
                NotificationUtil.showErrorNotification(notificationPane,"Ваш аккаунт был заблокирован");
                return;
            }
            Model.getInstance().setCurrentUser(new Users(response.getUser().getLogin(),response.getUser().getFirstName(),response.getUser().getLastName(),response.getUser().getEmail(),response.getUser().getWallet(),response.getUser().getLastLogin()));
            Stage stage = (Stage) enterBut.getScene().getWindow();
            Model.getInstance().getViewFactory().closeStage(stage);
            switch (response.getRole()){
                case "admin":
                    Model.getInstance().getViewFactory().showAdminWindow();
                    break;
                case "employee":
                    Model.getInstance().getViewFactory().showEmployeeWindow();
                    break;
                case "client":
                    Model.getInstance().getViewFactory().showClientWindow();
                    break;
            }
        });
    }
}