package com.robot.common;

import com.alibaba.fastjson.JSONObject;
import com.robot.enums.SmallGameType;
import com.robot.pojo.GroupMemberInfo;
import com.robot.pool.HttpClientResult;
import com.robot.pool.HttpClientUtils;
import com.sun.el.stream.Optional;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.message.BasicHeader;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

/**
 * @author Michael
 */
public class CommonUtils {
    private static CommonUtils instance;
    private CommonUtils(){

    }

    public static CommonUtils getInstance(){
        if (instance == null) {
            instance = new CommonUtils();
        }
        return instance;
    }

    public JSONObject getJSONParam(HttpServletRequest request){
        JSONObject jsonParam = null;
        try {
            // 获取输入流
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));

            // 写入数据到Stringbuilder
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = streamReader.readLine()) != null) {
                sb.append(line);
            }
            jsonParam = JSONObject.parseObject(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonParam;
    }

    public <T extends Enum<?>> T randomEnum(Class<T> clazz){
        Random random = new Random();
        int x = random.nextInt(clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[x];
    }

    public int getIntegerRandomId(List<Integer> list) {
        Random random = new Random();
        return list.get(random.nextInt(list.size()));
    }

    public String getStringRandomId(List<String> list) {
        Random random = new Random();
        return list.get(random.nextInt(list.size()));
    }

    public <K, V> Map.Entry<K, V> getTail(LinkedHashMap<K, V> map) {
        Iterator<Map.Entry<K, V>> iterator = map.entrySet().iterator();
        Map.Entry<K, V> tail = null;
        while (iterator.hasNext()) {
            tail = iterator.next();
        }
        return tail;
    }

    public <K, V> Map.Entry<K, V> getHead(LinkedHashMap<K, V> map) {
        return map.entrySet().iterator().next();
    }

    public void sendTextMsg(String chatRoomId,String content,String api_url) {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("wId", CommonConsts.getInstance().wId);
        jsonObject.put("wcId", chatRoomId);
        jsonObject.put("content", content);
        String sendUrl = api_url + "/sendText";

        Header header = new BasicHeader(HttpHeaders.AUTHORIZATION, CommonConsts.getInstance().authorization);
        HttpClientResult reslult = HttpClientUtils.doPostJson(sendUrl, jsonObject.toJSONString(), header);
        System.out.println("send message result = " + reslult.getContent());
    }
}
