package com.luooqi.ocr;

import cn.hutool.core.util.StrUtil;
import cn.hutool.log.StaticLog;
import com.luooqi.ocr.controller.ProcessController;
import com.luooqi.ocr.controller.SettingController;
import com.luooqi.ocr.model.Speaker;
import com.luooqi.ocr.model.StageInfo;
import com.luooqi.ocr.snap.ScreenCapture;
import com.luooqi.ocr.utils.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToolBar;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.jnativehook.GlobalScreen;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static javafx.application.Platform.runLater;

public class MainFm extends Application {

    private static StageInfo stageInfo;
    public static Stage stage;
    private static Scene mainScene;
    private static ScreenCapture screenCapture;
    private static ProcessController processController;
    private static SettingController settingController;
    private static TextArea textArea;
    private Integer audioSpeaker = 0;
    private Integer speakRate = 5;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        stageInfo = new StageInfo();
        stage.xProperty().addListener((observable, oldValue, newValue) -> {
            if (stage.getX() > 0) {
                stageInfo.setX(stage.getX());
            }
        });
        stage.yProperty().addListener((observable, oldValue, newValue) -> {
            if (stage.getY() > 0) {
                stageInfo.setY(stage.getY());
            }
        });
        screenCapture = new ScreenCapture(stage);

        // 创建进度指示器
        processController = new ProcessController();
        processController.initModality(Modality.APPLICATION_MODAL);
        processController.setWidth(300);
        processController.setHeight(150);

        settingController = new SettingController();
        settingController.setWidth(300);
        settingController.setHeight(200);
        settingController.initModality(Modality.APPLICATION_MODAL);

        // 初始化全局快捷键
        initKeyHook();
        // 创建托盘
        createTray(stage);

        HBox topBar = new HBox(
                CommUtils.createButton("settingBtn", () -> {
                    ResetPositionUtils.show(stage, settingController);
                    settingController.show();
                }, "设置"),
                CommUtils.createButton("snapBtn", MainFm::doSnap, "截图"),
                CommUtils.createButton("openImageBtn", MainFm::recImage, "打开"),
                CommUtils.createButton("copyBtn", this::copyText, "复制"),
                CommUtils.createButton("clearBtn", this::clearText, "清空"),
                CommUtils.createButton("text2AudioBtn", this::text2AudioBtn, "语音转换"),
                CommUtils.createLabel("语音:", null),
                CommUtils.createChoiceBox(Speaker.getNames(), ((observable, oldValue, newValue) -> audioSpeaker = Speaker.getValue(newValue)), 0, "语音库"),
                CommUtils.createLabel("音量:", null),
                CommUtils.createChoiceBox(Stream.iterate(1, item -> item + 1).limit(15).map(v -> v + "")
                        .collect(Collectors.toList()), ((observable, oldValue, newValue) -> speakRate = newValue.intValue() + 1), 4, "播放速度")
        );
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setId("topBar");
        topBar.setMinHeight(40);
        topBar.setSpacing(8);
        topBar.setPadding(new Insets(6, 8, 6, 8));

        textArea = new TextArea();
        textArea.setId("ocrTextArea");
        textArea.setWrapText(true);
        textArea.setBorder(new Border(new BorderStroke(Color.DARKGRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        textArea.setFont(Font.font("Arial", FontPosture.REGULAR, 14));

        ToolBar footerBar = new ToolBar();
        footerBar.setId("statsToolbar");
        Label statsLabel = new Label();
        SimpleStringProperty statsProperty = new SimpleStringProperty("总字数：0");
        textArea.textProperty().addListener((observable, oldValue, newValue) -> statsProperty.set("总字数：" + newValue.replaceAll(CommUtils.SPECIAL_CHARS, "").length()));
        statsLabel.textProperty().bind(statsProperty);
        footerBar.getItems().add(statsLabel);
        BorderPane root = new BorderPane();
        root.setTop(topBar);
        root.setCenter(textArea);
        root.setBottom(footerBar);
        root.getStylesheets().addAll(
                getClass().getResource("/css/main.css").toExternalForm()
        );
        CommUtils.initStage(primaryStage);
        mainScene = new Scene(root, 670, 470);
        stage.setScene(mainScene);
        stage.setResizable(false);
        stage.show();
    }

    /**
     * 创建托盘程序,awt
     */
    private void createTray(Stage stage) {

        try {
            // 阻止隐藏最后一个窗口后销毁进程
            Platform.setImplicitExit(false);

            // 要显示的菜单
            PopupMenu popupMenu = new PopupMenu();
            java.awt.MenuItem openItem = new java.awt.MenuItem("show");
            java.awt.MenuItem quitItem = new java.awt.MenuItem("quit");

            SystemTray tray = SystemTray.getSystemTray();
            BufferedImage image = ImageIO.read(this.getClass().getClassLoader().getResourceAsStream("img/logo.png"));
            TrayIcon trayIcon = new TrayIcon(image, "toolOcr");
            tray.add(trayIcon);
            trayIcon.setImageAutoSize(true);
            trayIcon.addMouseListener(new TrayListener(stage));

            // 事件监听器
            ActionListener acl = event -> {
                java.awt.MenuItem item = (java.awt.MenuItem) event.getSource();

                if (item.getLabel().equals("quit")) {
                    SystemTray.getSystemTray().remove(trayIcon);
                    Platform.exit();
                    System.exit(0);
                    return;
                }

                if (item.getLabel().equals("show")) {
                    Platform.runLater(stage::show);
                }
            };

            openItem.addActionListener(acl);
            quitItem.addActionListener(acl);

            popupMenu.add(openItem);
            popupMenu.add(quitItem);

            trayIcon.setPopupMenu(popupMenu);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void wrapText() {
        textArea.setWrapText(!textArea.isWrapText());
    }

    @Override
    public void stop() throws Exception {
        GlobalScreen.unregisterNativeHook();
    }

//    private static void setting() {
//        settingController.show();
//    }

    private void clearText() {
        textArea.setText("");
    }

    private void copyText() {
        String text = textArea.getSelectedText();
        if (StrUtil.isBlank(text)) {
            text = textArea.getText();
        }
        if (StrUtil.isBlank(text)) {
            return;
        }
        Map<DataFormat, Object> data = new HashMap<>();
        data.put(DataFormat.PLAIN_TEXT, text);
        Clipboard.getSystemClipboard().setContent(data);
    }

    public static void doSnap() {
        stageInfo.setWidth(stage.getWidth());
        stageInfo.setHeight(stage.getHeight());
        stageInfo.setFullScreenState(stage.isFullScreen());
        runLater(screenCapture::prepareForCapture);
    }

    private static void recImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Please Select Image File");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg"));
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile == null || !selectedFile.isFile()) {
            return;
        }
        stageInfo = new StageInfo(stage.getX(), stage.getY(),
                stage.getWidth(), stage.getHeight(), stage.isFullScreen());
        try {
            BufferedImage image = ImageIO.read(selectedFile);
            doOcr(image);
        } catch (IOException e) {
            StaticLog.error(e);
        }
    }

    public static void cancelSnap() {
        runLater(screenCapture::cancelSnap);
    }

    public static void doOcr(BufferedImage image) {
        String clientId = PrefsSingleton.get().get("ocr_key", "");
        String clientSecret = PrefsSingleton.get().get("ocr_secret", "");

        if (StrUtil.isBlank(clientId) || StrUtil.isBlank(clientSecret)) {
            Platform.runLater(() -> {
                AlertUtils.showErrorAlert(stage, "请先配置百度OCR密钥");
                restore(true);
            });
            return;
        }

        ResetPositionUtils.show(stage, processController);
        processController.show();
        Thread ocrThread = new Thread(() -> {
            byte[] bytes = CommUtils.imageToBytes(image);
            String text = OcrUtils.ocrImg(bytes, clientId, clientSecret);
            Platform.runLater(() -> {
                processController.close();
                textArea.setText(text);
                restore(true);
            });
        });
        ocrThread.setDaemon(false);
        ocrThread.start();
    }

    private void text2AudioBtn() {
        String text = textArea.getSelectedText();
        if (StrUtil.isBlank(text)) {
            text = textArea.getText();
        }
        if (StrUtil.isBlank(text)) {
            AlertUtils.showErrorAlert(stage, "文本内容不能为空!");
        } else {
            AudioUtils.text2Audio(stage, audioSpeaker, speakRate, processController, text);
        }
    }

    public static void restore(boolean focus) {
        stage.setAlwaysOnTop(false);
        stage.setScene(mainScene);
        stage.setFullScreen(stageInfo.isFullScreenState());
        stage.setX(stageInfo.getX());
        stage.setY(stageInfo.getY());
        stage.setWidth(stageInfo.getWidth());
        stage.setHeight(stageInfo.getHeight());
        if (focus) {
            stage.setOpacity(1.0f);
            stage.requestFocus();
        } else {
            stage.setOpacity(0.0f);
        }
    }

    private static void initKeyHook() {
        try {
            Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
            logger.setLevel(Level.WARNING);
            logger.setUseParentHandlers(false);
            GlobalScreen.setEventDispatcher(new VoidDispatchService());
            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeKeyListener(new GlobalKeyListener());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
