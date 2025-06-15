package com.courcach.corsewww.Controllers.User;

import com.courcach.Server.Services.User.ForgotPassword.ForgotPasswordRequest;
import com.courcach.Server.Services.User.ForgotPassword.ForgotPasswordResponce;
import com.courcach.corsewww.Models.ConnectionToServer;
import com.courcach.corsewww.Models.Model;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.util.Random;

public class MailCodeWindowController {
    private Stage verificationStage;
    private String email;
    private String correctCode;


    @FXML private TextField digit1;
    @FXML private TextField digit2;
    @FXML private TextField digit3;
    @FXML private TextField digit4;
    @FXML private TextField digit5;
    @FXML private TextField digit6;

    @FXML private Label statusMessageLabel;
    @FXML private Button verifyButton;
    @FXML private Button resendButton;
    @FXML private Button cancelButton;

    private TextField[] codeFields;

    public void setVerificationStage(Stage stage) {
        this.verificationStage = stage;
    }

    public void setVerificationData(String email, String correctCode) {
        this.email = email;
        this.correctCode = correctCode;
        System.out.println("Verification Code: " + correctCode + " (For debug purposes)");
    }

    @FXML
    public void initialize() {
        codeFields = new TextField[]{digit1, digit2, digit3, digit4, digit5, digit6};

        setupCodeInputBehavior();
        setupButtonActions();
    }

    private void setupCodeInputBehavior() {
        for (int i = 0; i < codeFields.length; i++) {
            final int currentIndex = i;
            TextField currentField = codeFields[currentIndex];
            currentField.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    if (!newValue.matches("\\d*")) {
                        currentField.setText(oldValue);
                    } else if (newValue.length() > 1) {
                        currentField.setText(newValue.substring(0, 1));
                        String remaining = newValue.substring(1);
                        fillRemainingFields(currentIndex + 1, remaining);
                    }
                    if (!newValue.isEmpty() && currentField.getText().length() == 1 && currentIndex < codeFields.length - 1) {
                        Platform.runLater(() -> codeFields[currentIndex + 1].requestFocus());
                    }
                }
            });

            currentField.setOnKeyReleased(new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent event) {
                    if (event.getCode() == KeyCode.BACK_SPACE && currentField.getText().isEmpty() && currentIndex > 0) {
                        Platform.runLater(() -> codeFields[currentIndex - 1].requestFocus());
                    }
                }
            });

            currentField.setOnKeyReleased(new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent event) {
                    if (event.getCode() == KeyCode.DELETE && currentField.getText().isEmpty() && currentIndex < codeFields.length -1) {
                        Platform.runLater(() -> codeFields[currentIndex + 1].requestFocus());
                    }
                }
            });
        }
    }

    private void fillRemainingFields(int startIndex, String textToFill) {
        for (int i = 0; i < textToFill.length(); i++) {
            if (startIndex + i < codeFields.length) {
                String digit = String.valueOf(textToFill.charAt(i));
                if (digit.matches("\\d")) {
                    codeFields[startIndex + i].setText(digit);
                } else {
                    break;
                }
            } else {
                break;
            }
        }
        if (startIndex + textToFill.length() < codeFields.length) {
            codeFields[startIndex + textToFill.length()].requestFocus();
        } else {
            codeFields[codeFields.length - 1].requestFocus();
        }
    }

    private void setupButtonActions() {
        verifyButton.setOnAction(event -> verifyCode());
        resendButton.setOnAction(event -> resendCode());
        cancelButton.setOnAction(event -> {
            if (verificationStage != null) {
                verificationStage.close();
            }
        });
    }

    private void verifyCode() {
        StringBuilder enteredCode = new StringBuilder();
        for (TextField field : codeFields) {
            enteredCode.append(field.getText());
        }

        if (enteredCode.length() != 6) {
            showStatusMessage("Пожалуйста, введите полный 6-значный код.", true);
            return;
        }
        ConnectionToServer conn = Model.getInstance().getConnectionToServer();
        conn.sendObject(new ForgotPasswordRequest("checkCodeFromEmail",Integer.valueOf(enteredCode.toString())));
        System.out.println(Integer.valueOf(enteredCode.toString()));
        ForgotPasswordResponce responce = (ForgotPasswordResponce) conn.receiveObject();
        if(responce.getResponce()){
            System.out.println(responce.getMessage());
        }else{
            System.out.println(responce.getMessage());
        }
    }

    private void resendCode() {
        showStatusMessage("Отправка кода заново...", false);
        clearCodeFields();
        System.out.println("Resend code requested for: " + email);
        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(e -> {
            this.correctCode = generateRandomCode(6);
            showStatusMessage("Новый код отправлен на вашу почту. Попробуйте снова.", false);
            System.out.println("New debug code: " + this.correctCode);
        });
        pause.play();
    }

    private void showStatusMessage(String message, boolean isError) {
        statusMessageLabel.setText(message);
        statusMessageLabel.getStyleClass().removeAll("error", "success");
        if (isError) {
            statusMessageLabel.getStyleClass().add("error");
        } else {
            statusMessageLabel.getStyleClass().add("success");
        }
    }

    private void clearCodeFields() {
        for (TextField field : codeFields) {
            field.clear();
        }
        codeFields[0].requestFocus();
    }

    private String generateRandomCode(int length) {
        StringBuilder code = new StringBuilder();
        Random rand = new Random();
        for (int i = 0; i < length; i++) {
            code.append(rand.nextInt(10));
        }
        return code.toString();
    }
}
