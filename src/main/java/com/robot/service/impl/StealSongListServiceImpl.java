package com.robot.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.robot.common.CommonConsts;
import com.robot.log.FileInterfaceLog;
import com.robot.pool.HttpClientResult;
import com.robot.pool.HttpClientUtils;
import com.robot.service.StealSongListService;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.message.BasicHeader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author zhang
 */
@Service
public class StealSongListServiceImpl implements StealSongListService {
    @Value("${api.url}")
    String api_url;

    @Override
    public void getSongVoiceMsg(long msgId, String length, String bufId, String fromUser) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("wId", CommonConsts.getInstance().wId);
        jsonObject.put("msgId",msgId);
        jsonObject.put("length",length);
        jsonObject.put("bufId",bufId);
        jsonObject.put("fromUser",fromUser);

        String url = api_url + "/getMsgVoice";
        Header header = new BasicHeader(HttpHeaders.AUTHORIZATION, CommonConsts.getInstance().authorization);

        HttpClientResult result = HttpClientUtils.doPostJson(url,jsonObject.toJSONString(),header);
        System.out.println(result);
        FileInterfaceLog.info(result.getContent());
    }
}
