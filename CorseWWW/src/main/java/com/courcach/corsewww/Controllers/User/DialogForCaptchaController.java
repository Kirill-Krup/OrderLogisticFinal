package com.courcach.corsewww.Controllers.User;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.Random;

public class DialogForCaptchaController {

    private Dialog dialog;
    private Random random = new Random();
    private int correctAnswer;
    private boolean isCorrectAnswer = false;

    @FXML
    private Button backButton;

    @FXML
    private TextField captchaAnswerField;

    @FXML
    private Label captchaProblemLabel;

    @FXML
    private Button submitButton;


    public void initialize() {
        generateAndSetCaptchaProblem();
        submitButton.setOnAction(event -> {
            String userAnswer = captchaAnswerField.getText();
            if (userAnswer.equals(String.valueOf(correctAnswer))) {
                isCorrectAnswer = true;
                Platform.runLater(() -> {
                    if (dialog != null) {
                        if (dialog != null) {
                            dialog.setResult(ButtonType.CLOSE);
                            dialog.close();
                        }
                    }
                });
            }else{
                captchaAnswerField.setText("");
                generateAndSetCaptchaProblem();
            }
        });
        backButton.setOnAction(event -> {
            Platform.runLater(() -> {
                if (dialog != null) {
                    if (dialog != null) {
                        dialog.setResult(ButtonType.CLOSE);
                        dialog.close();
                    }
                }
            });
        });
    }



    private void generateAndSetCaptchaProblem() {
        int num1 = random.nextInt(10) + 1;
        int num2 = random.nextInt(3) + 1;
        correctAnswer = (int) Math.pow(num1,num2);
        captchaProblemLabel.setText(num1 + " ^ " + num2 + " =  ?" );
    }


    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
    }

    public boolean getIsCorrectAnswer() {return isCorrectAnswer;}
}