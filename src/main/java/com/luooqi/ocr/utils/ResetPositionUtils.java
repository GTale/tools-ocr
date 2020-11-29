package com.luooqi.ocr.utils;

import javafx.stage.Stage;

/**
 * 重新设置相对位置
 */
public class ResetPositionUtils {

    public static void reset(Stage primaryStage, Stage alertStage) {
        double centerXPosition = primaryStage.getX() + primaryStage.getWidth() / 2d;
        double centerYPosition = primaryStage.getY() + primaryStage.getHeight() / 2d;
        alertStage.setX(centerXPosition - alertStage.getWidth() / 2d);
        alertStage.setY(centerYPosition - alertStage.getHeight() / 2d);
    }

    public static void moveOut(Stage stage, double x, double y) {
        stage.setX(x);
        stage.setY(y);
    }
}