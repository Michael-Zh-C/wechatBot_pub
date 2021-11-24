package com.robot.threads;

import com.alibaba.fastjson.JSONObject;
import com.robot.common.CommonConsts;
import com.robot.pool.HttpClientResult;
import com.robot.pool.HttpClientUtils;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.message.BasicHeader;

/**
 * @author Michael
 */
public class GuessSongSendVoiceMsgTask implements Runnable {
    private String api_url;
    private String fileName;
    private String chat_room_id;

    public GuessSongSendVoiceMsgTask(String chat_room_id,String api_url,String fileName) {
        this.api_url = api_url;
        this.fileName = fileName;
        this.chat_room_id = chat_room_id;
    }

    private String contentUrl = "http://106.12.174.25:8080/audios/";
    @Override
    public void run() {
        try {
            //延迟一下 尽量保证语音消息在文字消息之后
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //发语音消息
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("wId", CommonConsts.getInstance().wId);
        jsonObject.put("wcId", chat_room_id);
        jsonObject.put("content", contentUrl + fileName);
        jsonObject.put("length",1);
        String sendUrl = api_url + "/sendVoice";

        Header header = new BasicHeader(HttpHeaders.AUTHORIZATION, CommonConsts.getInstance().authorization);
        HttpClientResult reslult = HttpClientUtils.doPostJson(sendUrl, jsonObject.toJSONString(), header);
        System.out.println("send voice message result = " + reslult.getContent());
    }
}
