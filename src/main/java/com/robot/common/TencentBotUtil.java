package com.robot.common;


import com.alibaba.fastjson.JSONObject;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.tbp.v20190311.TbpClient;
import com.tencentcloudapi.tbp.v20190311.models.TextProcessRequest;
import com.tencentcloudapi.tbp.v20190311.models.TextProcessResponse;

public class TencentBotUtil {
    private final static String SECRET_ID = "AKIDoQP7ZkeGbgTzEG1UgcKlvoQlpc9PO6Yj";
    private final static String SECRET_KEY = "uY0B5i4AzWmlTu4xZuxjFU0ECtIQHsDG";

    public static String getAutoReply(String content) {
        try{
            // 实例化一个认证对象，入参需要传入腾讯云账户secretId，secretKey,此处还需注意密钥对的保密
            // 密钥可前往https://console.cloud.tencent.com/cam/capi网站进行获取
            Credential cred = new Credential(SECRET_ID, SECRET_KEY);
            // 实例化一个http选项，可选的，没有特殊需求可以跳过
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("tbp.tencentcloudapi.com");
            // 实例化一个client选项，可选的，没有特殊需求可以跳过
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            // 实例化要请求产品的client对象,clientProfile是可选的
            TbpClient client = new TbpClient(cred, "", clientProfile);
            // 实例化一个请求对象,每个接口都会对应一个request对象
            TextProcessRequest req = new TextProcessRequest();

            req.setBotId("ec796762-6f4a-46c8-b952-615114e2ec04");
            req.setBotEnv("release");
            req.setTerminalId("1");
            req.setInputText(content);

            // 返回的resp是一个TextProcessResponse的实例，与请求对象对应
            TextProcessResponse resp = client.TextProcess(req);
            // 输出json格式的字符串回包
            JSONObject jsonObject = JSONObject.parseObject(TextProcessResponse.toJsonString(resp));
            return jsonObject.getString("ResponseText");
        } catch (TencentCloudSDKException e) {
            System.out.println(e.toString());
        }
        return CommonReply.AUTO_FAIL_REPLY;
    }
}
