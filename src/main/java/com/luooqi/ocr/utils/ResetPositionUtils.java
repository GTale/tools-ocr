package com.luooqi.ocr.utils;

import javafx.stage.Stage;

/**
 * 重新设置相对位置
 */
public class ResetPositionUtils {

    public static void show(Stage primaryStage, Stage showStage) {
        double centerXPosition = primaryStage.getX() + primaryStage.getWidth() / 2d;
        double centerYPosition = primaryStage.getY() + primaryStage.getHeight() / 2d;
        showStage.setX(centerXPosition - showStage.getWidth() / 2d);
        showStage.setY(centerYPosition - showStage.getHeight() / 2d);
    }

    public static void hidden(Stage stage, double x, double y) {
        stage.setX(x);
        stage.setY(y);
    }
}