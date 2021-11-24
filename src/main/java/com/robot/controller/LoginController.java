package com.robot.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.zxing.WriterException;
import com.robot.common.CommonConsts;
import com.robot.dao.AuthorizationDao;
import com.robot.pojo.Authorization;
import com.robot.pool.HttpClientResult;
import com.robot.pool.HttpClientUtils;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.message.BasicHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

@Controller
public class LoginController {
    @Value("${api.url}")
    String api_url;
    @Value("${message.url}")
    String message_url;
    @Autowired
    AuthorizationDao authorizationDao;


    @RequestMapping("/doLogin")
    @ResponseBody
    public void doLogin() throws IOException, WriterException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("account","13261692712");
        jsonObject.put("password","hyfj30194619");

        String loginUrl = api_url + "/member/login";
        HttpClientResult result = HttpClientUtils.doPostJson(loginUrl,jsonObject.toJSONString());
        jsonObject.clear();
        jsonObject = JSONObject.parseObject(result.getContent());
        System.out.println("json = " + jsonObject);
        String auth = jsonObject.getJSONObject("data").getString("Authorization");
        System.out.println("auth = " + auth);
        CommonConsts.getInstance().authorization = auth;
        System.out.println("CommonConsts.authorization = " + CommonConsts.getInstance().authorization);

        Authorization authorization = new Authorization();
        authorization.setAuthorization(CommonConsts.getInstance().authorization);

        if (authorizationDao.selectCount(authorization) > 0){
            authorization.setId(1);
            authorizationDao.update(authorization);
        }

        authorizationDao.add(authorization);
    }

    @RequestMapping("/wechatLogin")
    @ResponseBody
    public void wechatLogin(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("wcId","");
        jsonObject.put("proxy","3");

        String loginUrl = api_url + "/iPadLogin";

        Header header = new BasicHeader(HttpHeaders.AUTHORIZATION, CommonConsts.getInstance().authorization);
        System.out.println("CommonConsts.authorization = " + CommonConsts.getInstance().authorization);
        HttpClientResult reslult = HttpClientUtils.doPostJson(loginUrl,jsonObject.toJSONString(),header);
        System.out.println("result = " + reslult);

        jsonObject.clear();
        jsonObject = JSONObject.parseObject(reslult.getContent());

        if (jsonObject.get("code").equals("1000")) {
            //登陆成功
            JSONObject jsonObject1 = JSONObject.parseObject(jsonObject.getString("data"));
            System.out.println("jsonObject1 = " + jsonObject1);
            CommonConsts.getInstance().wId = jsonObject1.getString("wId");
            System.out.println("wid = "+ CommonConsts.getInstance().wId);

            Authorization authorization = new Authorization();
            authorization.setId(1);
            authorization.setwId(CommonConsts.getInstance().wId);

            authorizationDao.update(authorization);
        } else {
            System.out.println("登录失败");
        }
    }

    @RequestMapping("/wechatLoginConfirm")
    @ResponseBody
    public void wechatLoginConfirm() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("wId",CommonConsts.getInstance().wId);

        String loginUrl = api_url + "/getIPadLoginInfo";

        Header header = new BasicHeader(HttpHeaders.AUTHORIZATION, CommonConsts.getInstance().authorization);
        HttpClientResult reslult = HttpClientUtils.doPostJson(loginUrl,jsonObject.toJSONString(),header);
        jsonObject.clear();

        jsonObject = JSONObject.parseObject(reslult.getContent());

        if (jsonObject.get("code").equals("1000")) {
            JSONObject jsonObject1 = JSONObject.parseObject(jsonObject.getString("data"));
            System.out.println("jsonObject1 = " + jsonObject1);
            CommonConsts.getInstance().wechatId = jsonObject1.getString("wcId");

            Authorization authorization = new Authorization();
            authorization.setId(1);
            authorization.setWechatId(CommonConsts.getInstance().wechatId);

            authorizationDao.update(authorization);
        }
    }

    @RequestMapping("/getFriendList")
    @ResponseBody
    public void getFriendList(){
        //第一步：初始化通讯录列表
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("wId",CommonConsts.getInstance().wId);

        String loginUrl = api_url + "/initAddressList";

        Header header = new BasicHeader(HttpHeaders.AUTHORIZATION, CommonConsts.getInstance().authorization);
        HttpClientResult reslult = HttpClientUtils.doPostJson(loginUrl,jsonObject.toJSONString(),header);
        jsonObject.clear();

        jsonObject = JSONObject.parseObject(reslult.getContent());
        if (!jsonObject.get("code").equals("1000")) {
            System.out.println("获取好友列表失败！");
            return;
        }

        //第二步：获取通讯录列表
        jsonObject.clear();
        jsonObject.put("wId",CommonConsts.getInstance().wId);

        String getFriendListUrl = api_url + "/getAddressList";

        HttpClientResult resultForGetList = HttpClientUtils.doPostJson(getFriendListUrl,jsonObject.toJSONString(),header);
        System.out.println("resultForGetList = " + resultForGetList);
    }

    @RequestMapping("/setHttpCallbackUrl")
    @ResponseBody
    public void setHttpCallbackUrl(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("httpUrl",message_url);

        String loginUrl = api_url + "/setHttpCallbackUrl";

        Header header = new BasicHeader(HttpHeaders.AUTHORIZATION, CommonConsts.getInstance().authorization);
        HttpClientResult reslult = HttpClientUtils.doPostJson(loginUrl,jsonObject.toJSONString(),header);
        jsonObject.clear();
        System.out.println("result = " + reslult);
    }
}
