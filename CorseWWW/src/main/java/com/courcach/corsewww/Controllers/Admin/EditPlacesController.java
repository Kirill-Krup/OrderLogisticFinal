package com.courcach.corsewww.Controllers.Admin;

import com.courcach.Server.Services.Admin.AdminRequest;
import com.courcach.Server.Services.ClassesForRequests.Places;
import com.courcach.corsewww.Models.ConnectionToServer;
import com.courcach.corsewww.Models.Model;
import com.courcach.corsewww.Views.NotificationUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class EditPlacesController {
    private List<Places> allPlaces;
    private ObservableList<Places> filteredPlaces = FXCollections.observableArrayList();

    @FXML
    private Button addNewPlace;

    @FXML
    private Button backFromWindow;

    @FXML
    private TableColumn<?, String> categoryColumn;

    @FXML
    private Button delMaterial;

    @FXML
    private Button addNewCategory;

    @FXML
    private TableColumn<Places, String> descriptionColumn;

    @FXML
    private Button editMaterial;

    @FXML
    private TableColumn<Places, String> materialColumn;

    @FXML
    private TableColumn<Places, Float> priceColumn;

    @FXML
    private TableColumn<Places, Integer> quantityColumn;

    @FXML
    private TextField searchMaterial;

    @FXML
    private TableView<Places> placesTable;

    @FXML
    private StackPane notificationPane;


    public void initialize(){
        ConnectionToServer connection = Model.getInstance().getConnectionToServer();
        connection.sendObject(new AdminRequest("takeAllPlaces"));
        allPlaces = (List<Places>) connection.receiveObject();
        filteredPlaces.setAll(allPlaces);
        updateInfo(filteredPlaces);
        connection.sendObject(new AdminRequest("giveMeAllCategories"));
        List<String> allCategories = (List<String>) connection.receiveObject();

        backFromWindow.setOnAction(event -> {
            Stage stage = (Stage) backFromWindow.getScene().getWindow();
            Model.getInstance().getViewFactory().showAdminWindow();
            Model.getInstance().getViewFactory().closeStage(stage);
        });

        delMaterial.setOnAction(event -> {
            Places place = filteredPlaces.get(placesTable.getSelectionModel().getSelectedIndex());
            if (place != null) {
                connection.sendObject(new AdminRequest("delPlace",place));
                filteredPlaces.remove(place);
                updateInfo(filteredPlaces);
            }else {
               NotificationUtil.showErrorNotification(notificationPane,"Вы не выбрали ни одного материала");
            }
        });

        searchMaterial.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredPlaces.clear();
            if (newValue == null || newValue.isEmpty()) {
                filteredPlaces.addAll(allPlaces);
            } else {
                String lowerCaseFilter = newValue.toLowerCase();
                for (Places place : allPlaces) {
                    if (place.getPlaceName().toLowerCase().contains(lowerCaseFilter) ||
                            place.getDescription().toLowerCase().contains(lowerCaseFilter) ||
                            place.getCategory().toLowerCase().contains(lowerCaseFilter)) {
                        filteredPlaces.add(place);
                    }
                }
            }
        });

        addNewPlace.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Admin/AddNewMaterialDialog.fxml"));
                DialogPane dialogPane = loader.load();

                AddNewMaterialDialogController controllerForAdd = loader.getController();
                Dialog<Void> dialog = new Dialog<>();
                dialog.setTitle("Добавление нового материала");
                dialog.setDialogPane(dialogPane);

                controllerForAdd.setDialog(dialog);
                controllerForAdd.renderComboBox(allCategories);
                controllerForAdd.setMaterialAddedCallback(newPlace -> {
                    try {
                        boolean nameExists = allPlaces.stream()
                                .anyMatch(place -> place.getPlaceName().equalsIgnoreCase(newPlace.getPlaceName()));
                       if (nameExists) {
                           NotificationUtil.showErrorNotification(notificationPane,"Такой товар уже существует");
                           return;
                       }
                        connection.sendObject(new AdminRequest("addPlace", newPlace));
                        allPlaces.add(newPlace);
                        filteredPlaces.setAll(allPlaces);
                        updateInfo(filteredPlaces);

                    } catch (Exception e) {
                        NotificationUtil.showErrorNotification(notificationPane,"Ошибка при добавлении");
                    }
                });
                dialog.showAndWait();

            } catch (IOException e) {
                NotificationUtil.showErrorNotification(notificationPane,"Ошибка загрузки диалога");
            }
        });

        addNewCategory.setOnAction(event -> {
            try{
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Admin/NewCategory.fxml"));
                DialogPane dialogPane = loader.load();
                NewCategoryController controllerForAdd = loader.getController();
                Dialog<Void> dialog = new Dialog<>();
                dialog.setTitle("Добавление новой категории");
                dialog.setDialogPane(dialogPane);
                controllerForAdd.setDialog(dialog);
                controllerForAdd.setCategoriesList(allCategories);
                controllerForAdd.setCategoryCallback(newCategory -> {
                    connection.sendObject(new AdminRequest("addCategory",newCategory));
                    allCategories.add(newCategory);
                });
                dialog.showAndWait();

            }catch (IOException e){
                NotificationUtil.showErrorNotification(notificationPane,"Ошибка загрузки диалога");
            }
        });

        editMaterial.setOnAction(event -> {
            Places selectedPlace = placesTable.getSelectionModel().getSelectedItem();
            if(selectedPlace == null) {
                NotificationUtil.showErrorNotification(notificationPane,"Выберите товар для редактирования");
                return;
            }
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Admin/EditPlaceDialogPane.fxml"));
                DialogPane dialogPane = loader.load();
                EditPlaceDialogPaneController controller = loader.getController();
                Dialog<Void> dialog = new Dialog<>();
                dialog.setTitle("Окно редактирования");
                dialog.setDialogPane(dialogPane);
                controller.setDialog(dialog);
                controller.setCategoriesList(allCategories);
                controller.setSelectedPlace(selectedPlace);
                controller.setMaterialAddedCallback(modifiedPlace -> {
                    if(modifiedPlace != null) {
                        connection.sendObject(new AdminRequest("editPlace", selectedPlace, modifiedPlace));
                        String request = (String) connection.receiveObject();
                        if(request.contains("обновлено")){
                            NotificationUtil.showNotification(notificationPane,request);
                        }else{
                            NotificationUtil.showErrorNotification(notificationPane,request);
                        }
                        int index = allPlaces.indexOf(selectedPlace);
                        if(index >= 0) {
                            allPlaces.set(index, modifiedPlace);
                            filteredPlaces.setAll(allPlaces);
                            updateInfo(filteredPlaces);
                        }
                    }
                });
                dialog.showAndWait();
            } catch (IOException e) {
                NotificationUtil.showErrorNotification(notificationPane,"Ошибка загрузки диалога");
            }
        });
    }


    private void updateInfo(ObservableList<Places> filteredPlaces) {
        materialColumn.setCellValueFactory(new PropertyValueFactory<>("placeName"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        placesTable.setRowFactory(tv -> new TableRow<Places>() {
            @Override
            protected void updateItem(Places place, boolean empty) {
                super.updateItem(place, empty);
                getStyleClass().remove("low-row");
                if (empty || place == null) {
                    return;
                }
                if (place.getQuantity()<10) {
                    getStyleClass().add("low-row");
                }
            }
        });
        placesTable.setItems(filteredPlaces);
    }
}
