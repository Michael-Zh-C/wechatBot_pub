package com.robot.common;

import com.robot.chatroom.ChatRoom;
import com.robot.dao.AuthorizationDao;
import com.robot.pojo.Authorization;
import com.robot.pojo.ProverbInfo;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Michael
 */
public class CommonConsts {
    @Autowired
    private AuthorizationDao authorizationDao;
    private static CommonConsts instance;

    private CommonConsts(){
//        System.out.println("authorizationDao = " + authorizationDao);
//        Authorization authorizationTemp = authorizationDao.findAuthor(1);
//        if (authorizationTemp != null){
//            authorization = authorizationTemp.getAuthorization();
//        }

    }
    public String authorization = "";
    public String wId = "";
    public String wechatId = "";
    public List<Integer> mapIds;
    public List<Integer> beautyImgIds;
    public Map<String, Map<String, String>> autoReplyDictionary;
    public Map<String,List<String>> pets;
    public Map<String, ProverbInfo> proverbInfos;
    public Map<String, ChatRoom> chatRooms = new HashMap<>();
    public List<Integer> songIds;
    public List<Integer> lotteryIds;
    public Map<String,Integer> todayLotteryRecord = new HashMap<>();


    public static CommonConsts getInstance(){
        if (instance == null) {
            instance = new CommonConsts();
        }
        return instance;
    }
}
