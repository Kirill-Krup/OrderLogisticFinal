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
               if(callback != null) {
                   this.selectedPlace = place;
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
        int count = 0;
        if(nameField.getText() != null){
            selectedPlace.setPlaceName(nameField.getText());
            count++;
        }
        if (descriptionArea.getText() != null){
            selectedPlace.setDescription(descriptionArea.getText());
            count++;
        }
        if(categoryCombo.getValue() != null){
            selectedPlace.setCategory(categoryCombo.getValue());
            count++;
        }
        if(!priceField.getText().trim().isEmpty() && priceField.getText()!= null){
            selectedPlace.setPrice(Float.parseFloat(priceField.getText()));
            count++;
        }
        if(!quantityField.getText().trim().isEmpty() && quantityField.getText() != null){
            selectedPlace.setQuantity(Integer.parseInt(quantityField.getText()));
            count++;
        }
        if(count<1){
            return Optional.empty();
        }else{
            return Optional.of(selectedPlace);
        }
    }
}
