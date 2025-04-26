package com.courcach.corsewww.Controllers;

import com.courcach.Server.Services.AuthRequest;
import com.courcach.Server.Services.AuthResponse;
import com.courcach.Server.Services.ClassesForRequests.Users;
import com.courcach.corsewww.Models.ConnectionToServer;
import com.courcach.corsewww.Models.Model;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class LoginController {
    @FXML
    private Button signUpBut;

    @FXML
    private Button enterBut;

    @FXML
    private Text errorLabel;

    @FXML
    private TextField loginInput;

    @FXML
    private TextField passwordInput;



    public void initialize() {
        signUpBut.setOnAction(e -> {
            Stage stage = (Stage) signUpBut.getScene().getWindow();
            Model.getInstance().getViewFactory().closeStage(stage);
            Model.getInstance().getViewFactory().showRegistrationWindow();
        });

        enterBut.setOnAction(e -> {
            String login = loginInput.getText();
            String password = passwordInput.getText();
            if(login.isEmpty() || password.isEmpty()) {
                showError("Пожалуйста, заполните все поля");
                return;
            }
            ConnectionToServer connection = Model.getInstance().getConnectionToServer();
            connection.sendObject(new AuthRequest(login,password,"LOGIN"));
            AuthResponse response = (AuthResponse) connection.receiveObject();
            if(!response.isSuccess()){
                showError("Вы заблокированы");
                return;
            }
            if (response.getRole() == null) {
                showError("Неверно введён логин или пароль");
                return;
            }
            Model.getInstance().setCurrentUser(new Users(response.getUser().getLogin(),response.getUser().getFirstName(),response.getUser().getLastName(),response.getUser().getEmail(),response.getUser().getWallet(),response.getUser().getPhotoPath()));
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
    private void showError(String error) {
        errorLabel.setVisible(true);
        errorLabel.setText(error);
      }
}