package com.courcach.corsewww.Controllers.Admin;

import com.courcach.Server.Services.ClassesForRequests.Places;
import com.courcach.corsewww.Controllers.Admin.CategoryInterface.MaterialAddedCallback;
import com.courcach.corsewww.Views.NotificationUtil;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import java.util.List;
import java.util.Optional;

public class AddNewMaterialDialogController {
    private Dialog dialog;
    private Places resultPlace = null;
    private MaterialAddedCallback callback;


    @FXML
    private Button addPlaceBut;

    @FXML
    private ComboBox<String> categoryBox;

    @FXML
    private Button delAllBut;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private Button exitBut;

    @FXML
    private TextField placeName;

    @FXML
    private TextField priceField;

    @FXML
    private TextField quantityField;

    @FXML
    private StackPane notificationPane;


    public void initialize() {
        addPlaceBut.setOnAction(event -> {
            Optional<Places> result = getResult();
            result.ifPresent(place -> {
                this.resultPlace = place;
                if (callback != null) {
                    callback.onMaterialAdded(place);
                }
                closeDialogPane();
            });
        });

        delAllBut.setOnAction(event -> {
            placeName.clear();
            priceField.clear();
            quantityField.clear();
            descriptionArea.clear();
            categoryBox.getSelectionModel().clearSelection();
        });

        exitBut.setOnAction(event -> {
            closeDialogPane();
        });
    }

    public Optional<Places> getResult() {
        String name = placeName.getText().trim();
        String description = descriptionArea.getText().trim();
        String priceText = priceField.getText().trim();
        String quantityText = quantityField.getText().trim();
        String category = categoryBox.getSelectionModel().getSelectedItem();

        if (name.isEmpty()) {
            showError("Название товара не может быть пустым!");
            return Optional.empty();
        }
        if (description.isEmpty()) {
            showError("Описание товара не может быть пустым!");
            return Optional.empty();
        }
        if (priceText.isEmpty()) {
            showError("Укажите цену!");
            return Optional.empty();
        }
        if (quantityText.isEmpty()) {
            showError("Укажите количество!");
            return Optional.empty();
        }
        if (category == null || category.trim().isEmpty()) {
            showError("Выберите категорию товара");
            return Optional.empty();
        }

        try {
            float price = Float.parseFloat(priceText);
            int quantity = Integer.parseInt(quantityText);

            if (price <= 0) {
                showError("Цена должна быть положительной!");
                return Optional.empty();
            }
            if (quantity <= 0) {
                showError("Количество должно быть положительным!");
                return Optional.empty();
            }

            return Optional.of(new Places(name, description, price, category, quantity));
        } catch (NumberFormatException e) {
            showError("Некорректный формат числа для цены или количества");
            return Optional.empty();
        }
    }

    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
    }

    public void renderComboBox(List<String> listOfPlaces) {
        categoryBox.getItems().setAll(listOfPlaces);
    }

    private void closeDialogPane(){
        if (dialog != null) {
            dialog.setResult(ButtonType.CLOSE);
            dialog.close();
        }
    }

    public void setMaterialAddedCallback(MaterialAddedCallback callback) {
        this.callback = callback;
    }

    private void showError(String message){
        NotificationUtil.showErrorNotification(notificationPane, message);
    }
}
