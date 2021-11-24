package com.robot.controller;

import com.robot.common.CommonConsts;
import com.robot.common.CommonGroupConst;
import com.robot.dao.*;
import com.robot.pojo.Authorization;
import com.robot.service.UserSummonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zhang
 */
@Controller
@RequestMapping("/init")
public class InitController {
    @Autowired
    AuthorizationDao authorizationDao;
    @Autowired
    BattleStageDao battleStageDao;
    @Autowired
    ImageDao imageDao;
    @Autowired
    MessageAutoReplyDao messageAutoReplyDao;
    @Autowired
    UserSummonService userSummonService;
    @Autowired
    ProverbInfoDao proverbInfoDao;
    @Autowired
    SongInfoDao songInfoDao;
    @Autowired
    LotteryRecordDao lotteryRecordDao;
    @Autowired
    LotteryInfoDao lotteryInfoDao;
    @Autowired
    GroupActivityInfoDao groupActivityInfoDao;

    @RequestMapping("/initData")
    @ResponseBody
    public void initData(){
        Authorization authorizationTemp = authorizationDao.findAuthor(1);
        if (authorizationTemp != null){
            CommonConsts.getInstance().authorization = authorizationTemp.getAuthorization();
            CommonConsts.getInstance().wechatId = authorizationTemp.getWechatId();
            CommonConsts.getInstance().wId = authorizationTemp.getwId();
            CommonConsts.getInstance().mapIds = battleStageDao.getStageIdList();
            CommonConsts.getInstance().beautyImgIds = imageDao.getBeautyImgIdList();
            CommonConsts.getInstance().autoReplyDictionary = messageAutoReplyDao.selectAll();
            CommonConsts.getInstance().pets = userSummonService.getAllPets();
            CommonConsts.getInstance().proverbInfos = proverbInfoDao.selectAll();
            CommonConsts.getInstance().songIds = songInfoDao.getIds();
            CommonConsts.getInstance().lotteryIds = lotteryInfoDao.getIds();

            initLotteryRecord();
            initChatCountMap();
        }
        System.out.println("authorization = " + CommonConsts.getInstance().authorization);
        System.out.println("wechatId = " + CommonConsts.getInstance().wechatId);
        System.out.println("wId = " + CommonConsts.getInstance().wId);
        System.out.println("mapids = " + CommonConsts.getInstance().mapIds);
        System.out.println("beautyImgIds = " + CommonConsts.getInstance().beautyImgIds);
    }

    /**
     * 更新今日抽签信息
     * */
    private void initLotteryRecord() {
        Map<String,Integer> map = lotteryRecordDao.getTodayLotteryRecord();
        for (String k:map.keySet()) {
            CommonConsts.getInstance().todayLotteryRecord.put(k,map.get(k));
        }
    }

    /**
     * 更新今日活跃信息
     * */
    private void initChatCountMap() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateTime = sdf.format(new Date());
        List<Map<String, Object>> list = groupActivityInfoDao.queryTodayAll(dateTime);

        for (Map<String, Object> map:list) {
            String chatRoomId = (String) map.get("chatRoomId");
            String userName = (String) map.get("userName");
            int msgCount = (Integer) map.get("msgCount");
            if (CommonGroupConst.chatCountMap.containsKey(chatRoomId)) {
                ConcurrentHashMap<String, AtomicInteger> map1 = CommonGroupConst.chatCountMap.get(chatRoomId);
                if (map1.containsKey(userName)) {
                    int count = map1.get(userName).get();
                    if (count <= msgCount) {
                        map1.put(userName,new AtomicInteger(msgCount));
                    }
                } else {
                    map1.put(userName,new AtomicInteger(msgCount));
                }
            } else {
                ConcurrentHashMap<String, AtomicInteger> map1 = new ConcurrentHashMap<>();
                map1.put(userName,new AtomicInteger(msgCount));
                CommonGroupConst.chatCountMap.put(chatRoomId,map1);
            }
        }

        System.out.println("chatCountMap = " + CommonGroupConst.chatCountMap);
    }
}
