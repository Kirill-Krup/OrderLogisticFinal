package com.courcach.corsewww.Controllers.Admin;

import com.courcach.Server.Services.Admin.AdminRequest;
import com.courcach.Server.Services.ClassesForRequests.Users;
import com.courcach.corsewww.Models.ConnectionToServer;
import com.courcach.corsewww.Models.Model;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.List;

public class EditUsersController {
    private List<Users> originalUsersList;
    private ObservableList<Users> filteredUsers = FXCollections.observableArrayList();

    @FXML
    private Text errorLabel;

    @FXML
    private Button backBut;

    @FXML
    private Button blockUserBut;

    @FXML
    private Button delUserBut;

    @FXML
    private TextField findUserField;

    @FXML
    private Button giveEmployeeStatusBut;

    @FXML
    private TableView<Users> listOfUsers;

    @FXML
    private TableColumn<Users, String> loginColumn;

    @FXML
    private TableColumn<Users, String> nameColumn;

    @FXML
    private TableColumn<Users, String> roleColumn;

    @FXML
    private TableColumn<Users, String> surnameColumn;

    @FXML
    private TableColumn<Users, String> emailColumn;

    @FXML
    private Button unlockUserBut;

    public void initialize() {
        ConnectionToServer connection = Model.getInstance().getConnectionToServer();
        connection.sendObject(new AdminRequest("takeAllUsers"));
        originalUsersList = (List<Users>) connection.receiveObject();
        filteredUsers.setAll(originalUsersList);
        updateInfo(filteredUsers);


        backBut.setOnAction(e -> {
            Stage stage = (Stage) backBut.getScene().getWindow();
            Model.getInstance().getViewFactory().closeStage(stage);
            Model.getInstance().getViewFactory().showAdminWindow();
        });

        blockUserBut.setOnAction(e -> {
            Users selectedUser = (Users) listOfUsers.getSelectionModel().getSelectedItem();
            if(selectedUser != null) {
                connection.sendObject(new AdminRequest("blockUser",selectedUser));
                selectedUser.setIsBlocked(true);
                updateInfo(filteredUsers);
            } else {
                errorLabel.setText("Вы никого не выбрали");
            }
        });

        unlockUserBut.setOnAction(e -> {
            Users selectedUser = (Users) listOfUsers.getSelectionModel().getSelectedItem();
            if(selectedUser != null) {
                connection.sendObject(new AdminRequest("unlockUser",selectedUser));
                selectedUser.setIsBlocked(false);
                updateInfo(filteredUsers);
            } else {
                errorLabel.setText("Вы никого не выбрали");
            }
        });

        delUserBut.setOnAction(e -> {
           Users selectedUser = (Users) listOfUsers.getSelectionModel().getSelectedItem();
           if(selectedUser != null) {
               connection.sendObject(new AdminRequest("delUser",selectedUser));
               filteredUsers.remove(selectedUser);
               updateInfo(filteredUsers);
           }else {
               errorLabel.setText("Вы никого не выбрали");
           }
        });

        giveEmployeeStatusBut.setOnAction(e -> {
            Users selectedUser = (Users) listOfUsers.getSelectionModel().getSelectedItem();
            if(selectedUser != null) {
                connection.sendObject(new AdminRequest("giveEmployeeStatus",selectedUser));
                selectedUser.setRole("employee");
                updateInfo(filteredUsers);
            }else {
                errorLabel.setText("Вы никого не выбрали");
            }
        });

        findUserField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredUsers.clear();
            if (newValue == null || newValue.isEmpty()) {
                filteredUsers.addAll(originalUsersList);
            } else {
                String lowerCaseFilter = newValue.toLowerCase();
                for (Users user : originalUsersList) {
                    if (user.getLogin().toLowerCase().contains(lowerCaseFilter) ||
                            user.getFirstName().toLowerCase().contains(lowerCaseFilter) ||
                            user.getLastName().toLowerCase().contains(lowerCaseFilter)) {
                        filteredUsers.add(user);
                    }
                }
            }
        });
    }


    private void updateInfo(List<Users> users) {
        loginColumn.setCellValueFactory(new PropertyValueFactory<>("login"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        surnameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        listOfUsers.setRowFactory(tv -> new TableRow<Users>() {
            @Override
            protected void updateItem(Users user, boolean empty) {
                super.updateItem(user, empty);
                getStyleClass().remove("blocked-row");
                if (empty || user == null) {
                    return;
                }
                if (user.getIsBlocked()) {
                    getStyleClass().add("blocked-row");
                }
            }
        });
        listOfUsers.setItems(filteredUsers);
    }
}
