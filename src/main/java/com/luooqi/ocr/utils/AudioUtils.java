package com.luooqi.ocr.utils;

import cn.hutool.core.io.StreamProgress;
import cn.hutool.http.HttpUtil;
import com.luooqi.ocr.controller.ProcessController;
import com.luooqi.ocr.model.CaptureInfo;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class AudioUtils {
    /*
     *   百度旧版语音的文档已经找不到了，下面是新版的文档。
     *
     *   tex	必填	合成的文本，使用UTF-8编码。小于2048个中文字或者英文数字。（文本在百度服务器内转换为GBK后，长度必须小于4096字节）
     *   tok	必填	开放平台获取到的开发者access_token（见上面的“鉴权认证机制”段落）
     *   cuid	必填	用户唯一标识，用来计算UV值。建议填写能区分用户的机器 MAC 地址或 IMEI 码，长度为60字符以内
     *   ctp	必填	客户端类型选择，web端填写固定值1
     *   lan	必填	固定值zh。语言选择,目前只有中英文混合模式，填写固定值zh
     *   spd[旧版:rate]	选填	语速，取值0-15，默认为5中语速
     *   pit	选填	音调，取值0-15，默认为5中语调
     *   vol	选填	音量，取值0-15，默认为5中音量
     *   per   （基础音库）	选填	度小宇=1，度小美=0，度逍遥（基础）=3，度丫丫=4
     *   per   （精品音库）	选填	度逍遥（精品）=5003，度小鹿=5118，度博文=106，度小童=110，度小萌=111，度米朵=103，度小娇=5
     *   aue	选填	3为mp3格式(默认)； 4为pcm-16k；5为pcm-8k；6为wav（内容同pcm-16k）; 注意aue=4或者6是语音识别要求的格式，但是音频内容不是语音识别要求的自然人发音，所以识别效果会受影响。
     * */
    public static void text2Audio(Stage stage, ProcessController processController, String text) {
        String fileUrl = "https://tts.baidu.com/text2audio?cuid=baike&lan=ZH&ctp=1&pdt=301&vol=9&spd=5&per=5118&tex=" + text;
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("MP3 files (*.mp3)", "*.mp3");
        fileChooser.getExtensionFilters().add(extFilter);
        File target = fileChooser.showSaveDialog(stage);
        if (target == null) {
            AlertUtils.showErrorAlert(stage, "文件名不能为空");
        } else {
            HttpUtil.downloadFile(fileUrl, target, new StreamProgress() {
                @Override
                public void start() {
                    processController.setX(CaptureInfo.ScreenMinX + (CaptureInfo.ScreenWidth - 300) / 2);
                    processController.setY(250);
                    processController.show();
                }

                @Override
                public void progress(long progressSize) {
                    //Console.log("已下载：{}", FileUtil.readableFileSize(progressSize));
                }

                @Override
                public void finish() {
                    processController.close();
                    AlertUtils.showInfoAlert(stage, "下载完成");
                }
            });
        }
    }
}
