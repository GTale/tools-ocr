package com.luooqi.ocr.controller;

import com.luooqi.ocr.utils.PrefsSingleton;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.prefs.Preferences;

/**
 * 全局设置窗口
 *
 * @author phantomscloud
 */
public class SettingController extends Stage {

    public SettingController() {
        Preferences prefs = PrefsSingleton.get();

        Label keyLbl = new Label("百度OCR_Key: ");
        TextField key = new TextField(prefs.get("ocr_key", ""));

        Label secretLbl = new Label("百度OCR_Secret: ");
        TextField secret = new TextField(prefs.get("ocr_secret", ""));

        Label ffmpegLbl = new Label("ffmpeg路径: ");
        TextField ffmpeg = new TextField(prefs.get("ffmpeg", ""));

        Button save = new Button("保存");
        save.setOnMouseClicked(event -> {
            prefs.put("ocr_key", key.getText());
            prefs.put("ocr_secret", secret.getText());
            prefs.put("ffmpeg", ffmpeg.getText());
            this.hide();
        });

        // setting the vertical and horizon gap for the components
        GridPane pane = new GridPane();
        pane.setHgap(5);
        pane.setVgap(5);
        pane.setAlignment(Pos.CENTER);

        pane.add(keyLbl,0,0);
        pane.add(key,1,0);
        pane.add(secretLbl,0,1);
        pane.add(secret,1,1);
        pane.add(ffmpegLbl,0,2);
        pane.add(ffmpeg,1,2);
        pane.add(save,1,3);

        Scene scene = new Scene(pane);
        setScene(scene);
    }
}
