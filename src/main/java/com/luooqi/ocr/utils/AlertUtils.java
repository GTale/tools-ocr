package com.luooqi.ocr.utils;

import javafx.beans.property.ObjectProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.util.function.Supplier;

public class AlertUtils {
    /**
     * 信息提示框
     *
     * @param message
     */
    public static void showInfoAlert(Stage primaryStage, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        show(primaryStage, alert, false);
    }

    /**
     * 等待信息提示框
     *
     * @param message
     */
    public static void showAndWaitInfoAlert(Stage primaryStage, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        show(primaryStage, alert, true);
    }

    /**
     * 注意提示框
     *
     * @param message
     */
    public static void showWarnAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setContentText(message);
        alert.show();
    }

    /**
     * 异常提示框
     *
     * @param message
     */
    public static void showErrorAlert(Stage primaryStage, String message) {
        showErrorAlert(primaryStage, message, null);
    }

    /**
     * 异常提示框
     *
     * @param message
     */
    public static void showErrorAlert(Stage primaryStage, String message, Supplier action) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        show(primaryStage, alert, true, action);
    }

    /**
     * 确定提示框
     *
     * @param message
     */
    @SuppressWarnings("unchecked")
    public static void showConfirmAlert(Stage primaryStage, String message, Supplier success, Supplier error) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText(message);
        show(primaryStage, alert, true, success, error);
    }

    private static void show(Stage primaryStage, Alert alert, boolean shouldWait) {
        show(primaryStage, alert, shouldWait, null, null);
    }

    private static void show(Stage primaryStage, Alert alert, boolean shouldWait, Supplier action) {
        show(primaryStage, alert, shouldWait, action, null);
    }

    private static void show(Stage primaryStage, Alert alert, boolean shouldWait, Supplier success, Supplier error) {
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        ResetPositionUtils.moveOut(stage, -10000, -10000);
        stage.show();
        stage.hide();
        ResetPositionUtils.reset(primaryStage, stage);
        stage.show();
        if (shouldWait) {
            alert.resultProperty().addListener(listener -> {
                ObjectProperty property = (ObjectProperty) listener;
                ButtonType buttonType = (ButtonType) property.getValue();
                if (buttonType == ButtonType.OK) {
                    if (success != null) {
                        success.get();
                    }
                } else {
                    if (error != null) {
                        error.get();
                    }
                }
            });
        }
    }
}
