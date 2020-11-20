package com.luooqi.ocr.utils;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.luooqi.ocr.model.TextBlock;

import java.awt.*;
import java.util.stream.Collectors;

/**
 * tools-ocr
 * Created by 何志龙 on 2019-03-22.
 * Modified By phantom on 2020-11-21.
 */
public class OcrUtils {

    // 百度Ocr的API
    private static String clientId = "";
    private static String clientSecret = "";

    private enum BdType {
        BASIC,
        BASIC_LOCATION
    }

    public static String ocrImg(byte[] imgData) {
        return bdGeneralOcr(imgData);
    }

    private static String bdGeneralOcr(byte[] imgData) {
        return bdBaseOcr(imgData, BdType.BASIC);
    }

    private static String bdAccurateOcr(byte[] imgData) {
        return bdBaseOcr(imgData, BdType.BASIC_LOCATION);
    }

    private static String bdBaseOcr(byte[] imgData, BdType type) {
        //Grant Token
        String grantTokenUrl = "https://aip.baidubce.com/oauth/2.0/token?grant_type=client_credentials&client_id=" + clientId + "&client_secret=" + clientSecret;
        HttpResponse tokenResponse = WebUtils.get(grantTokenUrl);
        JSONObject tokenJson = JSONUtil.parseObj(WebUtils.getSafeHtml(tokenResponse));
        String token = tokenJson.get("access_token").toString();

        // Post Image Data
        String baiDuOcrUrl = "https://aip.baidubce.com/rest/2.0/ocr/v1/" + (type == BdType.BASIC ? "general_basic" : "general") + "?access_token=" + token;
        String baseImgData = "image=" + URLUtil.encodeQuery(Base64.encode(imgData));
        HttpResponse response = WebUtils.postRaw(baiDuOcrUrl, baseImgData);
        return extractBdResult(WebUtils.getSafeHtml(response),type);
    }

    private static String extractBdResult(String html,BdType type) {
        if (StrUtil.isBlank(html)) {
            return "";
        }
        JSONObject jsonObject = JSONUtil.parseObj(html);
        if (jsonObject.getInt("errno", 0) != 0) {
            return "";
        }
        JSONArray jsonArray = jsonObject.getJSONArray("words_result");
        if(type == BdType.BASIC) {
            return  jsonArray.stream().map(v -> ((JSONObject) v).getStr("words")).collect(Collectors.joining());
        }else{
            return  CommUtils.combineTextBlocks(jsonArray.stream().map(v -> {
                JSONObject jObj = (JSONObject)v;
                TextBlock textBlock = new TextBlock();
                textBlock.setText(jObj.getStr("words").trim() + "\n");
                JSONObject location = jObj.getJSONObject("location");
                int top = location.getInt("top");
                int left = location.getInt("left");
                int width = location.getInt("width");
                int height = location.getInt("height");
                textBlock.setTopLeft(new Point(top, left));
                textBlock.setTopRight(new Point(top, left + width));
                textBlock.setBottomLeft(new Point(top + height, left));
                textBlock.setBottomRight(new Point(top + height, left + width));
                return textBlock;
            }).collect(Collectors.toList()), false);
        }
    }

}
