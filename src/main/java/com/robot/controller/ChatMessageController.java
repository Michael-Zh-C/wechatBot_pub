package com.robot.controller;

import com.alibaba.fastjson.JSONObject;
import com.robot.chatroom.ChatRoom;
import com.robot.common.CommonConsts;
import com.robot.common.CommonUtils;
import com.robot.common.XmlParser;
import com.robot.service.StealSongListService;
import com.robot.threads.AutoIncreaseChatCountTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Michael
 */
@Controller
@RequestMapping("/chatMessage")
public class ChatMessageController {

    @Value("${api.url}")
    String api_url;

    @Autowired
    StealSongListService stealSongListService;

    @RequestMapping(value = "/data", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public void getByRequest(HttpServletRequest request) {

        //获取到JSONObject
        JSONObject jsonParam = CommonUtils.getInstance().getJSONParam(request);
        System.out.println("jsonParam = " + jsonParam);

        String messageType = jsonParam.getString("messageType");
        System.out.println("messageType = " + messageType);
        {
            JSONObject jsonObject1 = JSONObject.parseObject(jsonParam.getString("data"));
            System.out.println("msgType = " + jsonObject1.getString("msgType"));
        }

        if (messageType.equals("9")) {
            JSONObject jsonObject1 = JSONObject.parseObject(jsonParam.getString("data"));

            String fromGroup = jsonObject1.getString("fromGroup");
            String fromUser = jsonObject1.getString("fromUser");

            //统计每个群的活跃度
            AutoIncreaseChatCountTask task = new AutoIncreaseChatCountTask(fromGroup,fromUser);
            new Thread(task).start();
            if (!CommonConsts.getInstance().chatRooms.containsKey(fromGroup)) {
                ChatRoom chatRoom = new ChatRoom(fromGroup,api_url);
                CommonConsts.getInstance().chatRooms.put(fromGroup,chatRoom);
            }

            ChatRoom chatRoom = CommonConsts.getInstance().chatRooms.get(fromGroup);
            chatRoom.handleMessage(jsonObject1.getString("content"),fromUser);
        }

        if (messageType.equals("12")) {
            JSONObject jsonObject1 = JSONObject.parseObject(jsonParam.getString("data"));

            String fromGroup = jsonObject1.getString("fromGroup");
            String fromUser = jsonObject1.getString("fromUser");

            if ("20231982895@chatroom".equals(fromGroup) && "wxid_hzmzd2gcc86j22".equals(fromUser)) {
                //二狗发的语音，偷歌名
                System.out.println("二狗发的语音，偷歌名++++++++++++++++++++++++");
                long msgId = jsonObject1.getLong("msgId");

                String content = jsonObject1.getString("content");
                Map<String,Object> map = new HashMap<>();
                try {
                    map = XmlParser.parseXml(content);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                String length = (String) map.get("length");
                String bufId = (String) map.get("bufid");

                stealSongListService.getSongVoiceMsg(msgId,length,bufId,fromUser);
            }
        }
    }


}


