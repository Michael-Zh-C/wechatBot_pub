package com.robot.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.robot.common.CommonConsts;
import com.robot.common.CommonGroupConst;
import com.robot.common.MapSortUtil;
import com.robot.dao.GroupActivityInfoDao;
import com.robot.dao.GroupMemberInfoDao;
import com.robot.pojo.GroupActivityInfo;
import com.robot.pojo.GroupMemberInfo;
import com.robot.pool.HttpClientResult;
import com.robot.pool.HttpClientUtils;
import com.robot.service.GroupChatService;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.message.BasicHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Michael
 */
@Service
public class GroupChatServiceImpl implements GroupChatService {
    @Autowired
    GroupMemberInfoDao groupMemberInfoDao;
    @Autowired
    GroupActivityInfoDao groupActivityInfoDao;
    @Value("${api.url}")
    String api_url;
    @Override
    public String getGroupChatChampion(String groupId) {
        System.out.println("CommonGroupConst.chatCountMap. = " + CommonGroupConst.chatCountMap.keySet());
        if (!CommonGroupConst.chatCountMap.containsKey(groupId)) {
            System.out.println("groupId = " + groupId);
            return "当前群组无人说话";
        }

        ConcurrentHashMap<String, AtomicInteger> map = CommonGroupConst.chatCountMap.get(groupId);
        Map<String,Integer> map1 = new HashMap<>();
        for (String key:map.keySet()) {
            map1.put(key,map.get(key).get());
        }

        LinkedHashMap<String, Integer> sorted = (LinkedHashMap<String, Integer>) MapSortUtil.sortByValueDesc(map1);

        int i = 0;
        int target = sorted.size()>=3?3:sorted.size();
        StringBuffer sb = new StringBuffer();
        sb.append("本群最能叭叭的前三名分别为：\n");
        for (String key:sorted.keySet()) {
            if (i >= target) {
                break;
            }
            GroupMemberInfo groupMemberInfo = new GroupMemberInfo();
            groupMemberInfo.setChatRoomId(groupId);
            groupMemberInfo.setUserName(key);
            System.out.println("groupId = " + groupId + ",userName = " + key);

            String nickName = groupMemberInfoDao.queryNickName(groupMemberInfo);
            sb.append(nickName);
            sb.append("，发言数");
            sb.append(sorted.get(key));
            sb.append("\n");
            i++;
        }
        sb.append("让我们恭喜他们！希望他们再接再厉，让群更加活跃起来！特赠予本群龙王称号！\n");
        sb.append("如果想关闭播报功能，请群管理员输入“!播报 off”");

        return sb.toString();
    }

    @Override
    public void updateGroupMemberInfo(String groupId) {
        getChatRoomMember(groupId);
        getChatRoomInfo(groupId);
        getChatRoomLastActivity();
    }

    @Override
    public void updateGroupActivityInfo() {
        for (String chatRoomId: CommonGroupConst.chatCountMap.keySet()) {
            ConcurrentHashMap<String, AtomicInteger> map = CommonGroupConst.chatCountMap.get(chatRoomId);
            for (String userName: map.keySet()) {
                int count = map.get(userName).get();
                GroupActivityInfo groupActivityInfo = new GroupActivityInfo();
                groupActivityInfo.setChatRoomId(chatRoomId);
                groupActivityInfo.setUserName(userName);
                groupActivityInfo.setMsgCount(count);

                //设置时间
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String time = sdf.format(new Date());
                try {
                    groupActivityInfo.setTime(sdf.parse(time).getTime());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                groupActivityInfoDao.add(groupActivityInfo);
            }
        }
    }

    /**
     * 获取群成员信息
     * */
    private void getChatRoomMember(String chatRoomId) {
        System.out.println("获取群成员信息开始，当前群" + chatRoomId);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("wId", CommonConsts.getInstance().wId);
        jsonObject.put("chatRoomId", chatRoomId);
        String sendUrl = api_url + "/getChatRoomMember";

        Header header = new BasicHeader(HttpHeaders.AUTHORIZATION, CommonConsts.getInstance().authorization);
        HttpClientResult reslult = HttpClientUtils.doPostJson(sendUrl, jsonObject.toJSONString(), header);
        jsonObject = JSONObject.parseObject(reslult.getContent());

        if (jsonObject.get("code").equals("1000")) {
            JSONArray json = JSONArray.parseArray(jsonObject.getString("data"));
            System.out.println("json.size() = " + json.size());

            for (int i = 0 ; i <json.size() ; i++) {
                JSONObject jo = JSONObject.parseObject(json.getString(i));
                GroupMemberInfo groupMemberInfo = new GroupMemberInfo();
                groupMemberInfo.setChatRoomId(jo.getString("chatRoomId"));
                groupMemberInfo.setUserName(jo.getString("userName"));
                groupMemberInfo.setNickName(jo.getString("nickName"));
                groupMemberInfo.setDisplayName(jo.getString("displayName"));
                groupMemberInfo.setBigHeadImgUrl(jo.getString("bigHeadImgUrl"));
                groupMemberInfo.setSmallHeadImgUrl(jo.getString("smallHeadImgUrl"));

                groupMemberInfoDao.add(groupMemberInfo);
            }
        }
    }

    /**
     * 更新管理员信息
     * */
    private void getChatRoomInfo(String chatRoomId) {
        System.out.println("更新管理员信息开始，当前群" + chatRoomId);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("wId", CommonConsts.getInstance().wId);
        jsonObject.put("chatRoomId", chatRoomId);
        String sendUrl = api_url + "/getChatRoomInfo";

        Header header = new BasicHeader(HttpHeaders.AUTHORIZATION, CommonConsts.getInstance().authorization);
        HttpClientResult reslult = HttpClientUtils.doPostJson(sendUrl, jsonObject.toJSONString(), header);
        jsonObject = JSONObject.parseObject(reslult.getContent());

        if (jsonObject.get("code").equals("1000")) {
            JSONArray jo1 = JSONArray.parseArray(jsonObject.getString("data"));
            JSONArray json = JSONArray.parseArray(jo1.getJSONObject(0).getString("chatRoomMembers"));
            System.out.println("json.size() = " + json.size());

            for (int i = 0 ; i <json.size() ; i++) {
                JSONObject jo = JSONObject.parseObject(json.getString(i));
                if("true".equals(jo.getString("isManage"))) {
                    GroupMemberInfo groupMemberInfo = new GroupMemberInfo();
                    groupMemberInfo.setChatRoomId(chatRoomId);
                    groupMemberInfo.setUserName(jo.getString("userName"));
                    groupMemberInfo.setIsManager(1);

                    groupMemberInfoDao.edit(groupMemberInfo);
                }
            }
        }
    }

    /**
     * 更新群成员最后活跃时间
     * */
    private void getChatRoomLastActivity(){
        System.out.println("更新群成员最后活跃时间开始");

        for (String chatRoomId: CommonGroupConst.chatLastActiveMap.keySet()) {
            ConcurrentHashMap<String, Date> map = CommonGroupConst.chatLastActiveMap.get(chatRoomId);
            for (String userName:map.keySet()) {
                GroupMemberInfo groupMemberInfo = new GroupMemberInfo();
                groupMemberInfo.setUserName(userName);
                groupMemberInfo.setChatRoomId(chatRoomId);
                groupMemberInfo.setLastActivity(map.get(userName));

                groupMemberInfoDao.edit(groupMemberInfo);
            }
        }

        CommonGroupConst.chatLastActiveMap.clear();
    }



}
