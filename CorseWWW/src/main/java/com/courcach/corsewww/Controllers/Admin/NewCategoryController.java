package com.courcach.corsewww.Controllers.Admin;

import com.courcach.corsewww.Controllers.Admin.CategoryInterface.CategoryCallback;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.util.List;
import java.util.Optional;

public class NewCategoryController {

    @FXML
    private Button addButton;

    @FXML
    private Text errorLabel;

    @FXML
    private TextField addNewCategoryField;

    @FXML
    private Button exitButton;

    private Dialog dialog;
    private List<String> allCategories;
    private CategoryCallback callback;

    public void initialize() {
        exitButton.setOnAction(event->{
            closeDialogWindow();
        });

        addButton.setOnAction(event->{
            String category = addNewCategoryField.getText();
            if(!checkCategory(category)){
                Optional<String> resultCategory = getResultCategory();
                resultCategory.ifPresent(newCategory -> {
                    if(callback != null){
                        callback.categoryAdded(newCategory);
                    }
                    closeDialogWindow();
                });
            }else {
                errorLabel.setText("Такая категория уже существует");
            }
        });
    }

    public void setCategoryCallback(CategoryCallback callback) {
        this.callback = callback;
    }

    public Optional<String> getResultCategory() {
        String category = addNewCategoryField.getText();
        return Optional.of(category);
    }

    private boolean checkCategory(String category) {
        for (String c : allCategories) {
            if (c.equals(category)) {
                errorLabel.setText("Такая категория уже существует");
                return true;
            }
        }
        return false;
    }

    private void closeDialogWindow(){
        if(dialog != null){
            dialog.setResult(ButtonType.CLOSE);
            dialog.close();
        } else{
            System.out.println("Ошибка закрытия диалогового окна (null)");
        }
    }

    public void setDialog(Dialog<Void> dialog) {
        this.dialog = dialog;
    }

    public void setCategoriesList(List<String> allCategories) {
        this.allCategories = allCategories;
    }
}
