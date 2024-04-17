package com.weixin.njuteam.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.weixin.njuteam.util.baiduUtil.*;

import java.net.URLEncoder;

/**
 * 通用文字识别（高精度版）
 */
public class AccurateBasicUtil {

    /**
     * 重要提示代码中所需工具类
     * FileUtil,Base64Util,HttpUtil,GsonUtils请从
     * https://ai.baidu.com/file/658A35ABAB2D404FBF903F64D47C1F72
     * https://ai.baidu.com/file/C8D81F3301E24D2892968F09AE1AD6E2
     * https://ai.baidu.com/file/544D677F5D4E4F17B4122FBD60DB82B3
     * https://ai.baidu.com/file/470B3ACCA3FE43788B5A963BF0B625F3
     * 下载
     */
    public static String[] accurateBasic(String imgName) {
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/ocr/v1/accurate_basic";
        try {
            // 本地文件路径
            String filePath = imgName;
            byte[] imgData = BaiduFileUtil.readFileByBytes(filePath);
            String imgStr = Base64Util.encode(imgData);
            String imgParam = URLEncoder.encode(imgStr, "UTF-8");

            String param = "image=" + imgParam;

            // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
//            String accessToken = "[调用鉴权接口获取的token]";
            String accessToken = AuthServiceUtil.getAuth("YvnQYnkKrkRY3av6fqbfr7xm","Gyhyc3BilGcq6ate0hh56OAQ95IwA5kc");

            String result = HttpUtil.post(url, accessToken, param);
//            System.out.println(result);
            JSONObject jsonObject = JSONObject.parseObject(result);
//            System.out.println(jsonObject);
            JSONArray words_result = jsonObject.getJSONArray("words_result");
//            System.out.println(words_result);

            String[] lines = new String[words_result.size()];
            int i = 0;
            for (Object o : words_result) {
                JSONObject jo = (JSONObject) o;
                lines[i++] = jo.getString("words");
//                System.out.println(jo);
            }

//            System.out.println(Arrays.toString(lines));


            return lines;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
