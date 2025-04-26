package com.courcach.corsewww.Views;

import javafx.animation.*;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class NotificationUtil {
    public static void showErrorNotification(StackPane rootPane, String message) {
        StackPane notificationPane = new StackPane();
        notificationPane.getStyleClass().add("error-notification-pane");

        Label notificationLabel = new Label(message);
        notificationLabel.getStyleClass().add("error-notification-label");

        notificationPane.getChildren().add(notificationLabel);

        notificationPane.setMaxWidth(Double.MAX_VALUE);
        notificationPane.setTranslateY(-100);
        notificationPane.setOpacity(0);

        StackPane.setAlignment(notificationPane, javafx.geometry.Pos.TOP_CENTER);
        rootPane.getChildren().add(notificationPane);

        TranslateTransition slideIn = new TranslateTransition(Duration.millis(300), notificationPane);
        slideIn.setToY(0);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), notificationPane);
        fadeIn.setToValue(1);

        ParallelTransition showTransition = new ParallelTransition(slideIn, fadeIn);

        TranslateTransition slideOut = new TranslateTransition(Duration.millis(300), notificationPane);
        slideOut.setToY(-100);
        slideOut.setDelay(Duration.seconds(3));

        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), notificationPane);
        fadeOut.setToValue(0);
        fadeOut.setDelay(Duration.seconds(3));

        ParallelTransition hideTransition = new ParallelTransition(slideOut, fadeOut);
        hideTransition.setOnFinished(e -> rootPane.getChildren().remove(notificationPane));

        showTransition.play();
        hideTransition.play();
    }


    public static void showNotification(StackPane rootPane, String message) {
        StackPane notificationPane = new StackPane();
        notificationPane.getStyleClass().add("notification-pane");
        Label notificationLabel = new Label(message);
        notificationLabel.getStyleClass().add("notification-label");
        notificationPane.getChildren().add(notificationLabel);
        notificationPane.setMaxWidth(Double.MAX_VALUE);
        notificationPane.setTranslateY(-100);
        notificationPane.setOpacity(0);
        StackPane.setAlignment(notificationPane, javafx.geometry.Pos.TOP_CENTER);
        rootPane.getChildren().add(notificationPane);
        TranslateTransition slideIn = new TranslateTransition(Duration.millis(300), notificationPane);
        slideIn.setToY(0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), notificationPane);
        fadeIn.setToValue(1);
        ParallelTransition showTransition = new ParallelTransition(slideIn, fadeIn);
        TranslateTransition slideOut = new TranslateTransition(Duration.millis(300), notificationPane);
        slideOut.setToY(-100);
        slideOut.setDelay(Duration.seconds(3));
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), notificationPane);
        fadeOut.setToValue(0);
        fadeOut.setDelay(Duration.seconds(3));
        ParallelTransition hideTransition = new ParallelTransition(slideOut, fadeOut);
        hideTransition.setOnFinished(e -> rootPane.getChildren().remove(notificationPane));
        showTransition.play();
        hideTransition.play();
    }
}

