package com.luooqi.ocr.utils;

import javafx.application.Platform;
import javafx.stage.Stage;

import java.awt.event.InputEvent;
import java.awt.event.MouseListener;

public class TrayListener implements MouseListener {
    private Stage stage;

    public TrayListener(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void mouseClicked(java.awt.event.MouseEvent e) {
        // 非右键事件
        if ((e.getModifiers() & InputEvent.BUTTON3_MASK) == 0) {
            if (stage.isShowing()) {
                Platform.runLater(stage::hide);
            } else {
                Platform.runLater(stage::show);
            }
        }
    }

    @Override
    public void mousePressed(java.awt.event.MouseEvent e) {

    }

    @Override
    public void mouseReleased(java.awt.event.MouseEvent e) {

    }

    @Override
    public void mouseEntered(java.awt.event.MouseEvent e) {

    }

    @Override
    public void mouseExited(java.awt.event.MouseEvent e) {

    }
}