package com.courcach.corsewww.Controllers.Admin;


import com.courcach.Server.Services.ClassesForRequests.Places;
import com.courcach.corsewww.Controllers.Admin.CategoryInterface.MaterialAddedCallback;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;



import java.util.List;
import java.util.Optional;

public class EditPlaceDialogPaneController {
    private Dialog dialog;
    private List<String> allCategories;
    private Places selectedPlace;
    private MaterialAddedCallback callback;

    @FXML
    private Button cancelButton;

    @FXML
    private ComboBox<String> categoryCombo;

    @FXML
    private Label currentCategoryLabel;

    @FXML
    private Label currentDescriptionLabel;

    @FXML
    private Label currentNameLabel;

    @FXML
    private Label currentPriceLabel;

    @FXML
    private Label currentQuantityLabel;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private TextField nameField;

    @FXML
    private TextField priceField;

    @FXML
    private TextField quantityField;

    @FXML
    private Button saveButton;

    public void initialize() {

        cancelButton.setOnAction(event -> closeDialogWindow());

        saveButton.setOnAction(event->{
            Optional<Places> result = getResultPlace();
            result.ifPresent(place -> {
                if (callback != null) {
                    callback.onMaterialAdded(place);
                }
                closeDialogWindow();
            });
        });
    }

    private void closeDialogWindow(){
        if(dialog != null){
            dialog.setResult(ButtonType.CLOSE);
            dialog.close();
        } else{
            System.out.println("Ошибка закрытия диалогового окна (null)");
        }
    }

    private void initializeMyText(){
        currentNameLabel.setText(selectedPlace.getPlaceName());
        currentDescriptionLabel.setText(selectedPlace.getDescription());
        currentCategoryLabel.setText(selectedPlace.getCategory());
        currentPriceLabel.setText(String.valueOf(selectedPlace.getPrice()));
        currentQuantityLabel.setText(String.valueOf(selectedPlace.getQuantity()));
        categoryCombo.getItems().setAll(allCategories);
    }

    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
    }

    public void setCategoriesList(List<String> allCategories) {
        this.allCategories =  allCategories;
    }

    public void setSelectedPlace(Places selectedPlace) {
        this.selectedPlace = selectedPlace;
        initializeMyText();
    }

    public void setMaterialAddedCallback(MaterialAddedCallback callback) {
        this.callback = callback;
    }

    private Optional<Places> getResultPlace(){
        Places modifiedPlace = new Places(selectedPlace);
        boolean isModified = false;

        if(!nameField.getText().isEmpty() && !nameField.getText().equals(selectedPlace.getPlaceName())){
            modifiedPlace.setPlaceName(nameField.getText());
            isModified = true;
        }
        if(!descriptionArea.getText().isEmpty() && !descriptionArea.getText().equals(selectedPlace.getDescription())){
            modifiedPlace.setDescription(descriptionArea.getText());
            isModified = true;
        }
        if(categoryCombo.getValue() != null && !categoryCombo.getValue().equals(selectedPlace.getCategory())){
            modifiedPlace.setCategory(categoryCombo.getValue());
            isModified = true;
        }
        if(!priceField.getText().trim().isEmpty()){
            try {
                float newPrice = Float.parseFloat(priceField.getText());
                if(newPrice != selectedPlace.getPrice()) {
                    modifiedPlace.setPrice(newPrice);
                    isModified = true;
                }
            } catch (NumberFormatException e) {
                return Optional.empty();
            }
        }
        if(!quantityField.getText().trim().isEmpty()){
            try {
                int newQuantity = Integer.parseInt(quantityField.getText());
                if(newQuantity != selectedPlace.getQuantity()) {
                    modifiedPlace.setQuantity(newQuantity);
                    isModified = true;
                }
            } catch (NumberFormatException e) {
                return Optional.empty();
            }
        }

        return isModified ? Optional.of(modifiedPlace) : Optional.empty();
    }
}
