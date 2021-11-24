package com.robot.schedule;

import com.alibaba.fastjson.JSONObject;
import com.robot.common.CommonConsts;
import com.robot.common.CommonGroupConst;
import com.robot.dao.ConstellationInfoDao;
import com.robot.dao.GroupAnalysisSwitchDao;
import com.robot.dao.LotteryRecordDao;
import com.robot.enums.Constellation;
import com.robot.pojo.ConstellationInfo;
import com.robot.pool.HttpClientResult;
import com.robot.pool.HttpClientUtils;
import com.robot.service.GroupChatService;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.message.BasicHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Michael
 */
@Configuration
@EnableScheduling
public class Task {
    @Value("${api.url}")
    String api_url;

    @Autowired
    GroupAnalysisSwitchDao groupAnalysisSwitchDao;
    @Autowired
    GroupChatService groupChatService;
    @Autowired
    LotteryRecordDao lotteryRecordDao;

    @Scheduled(cron = "0 0 0 * * ?")
    private void getMemberChatCount() {
        //清空今日抽签信息
        lotteryRecordDao.deleteAll();
        CommonConsts.getInstance().todayLotteryRecord.clear();

        List<Map<String,Object>> groupInfos = groupAnalysisSwitchDao.selectAll();

        for (Map<String,Object> map:groupInfos) {
            groupChatService.updateGroupMemberInfo((String) map.get("chatRoomId"));

            int state = (int) map.get("state");
            if (state == 0) {
                String groupChatInfo = groupChatService.getGroupChatChampion((String) map.get("chatRoomId"));

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("wId", CommonConsts.getInstance().wId);
                jsonObject.put("wcId", map.get("chatRoomId"));
                jsonObject.put("content", groupChatInfo);
                String sendUrl = api_url + "/sendText";

                Header header = new BasicHeader(HttpHeaders.AUTHORIZATION, CommonConsts.getInstance().authorization);
                HttpClientResult result = HttpClientUtils.doPostJson(sendUrl, jsonObject.toJSONString(), header);
                System.out.println("send message result = " + result.getContent());
            }
        }
        groupChatService.updateGroupActivityInfo();

        CommonGroupConst.chatCountMap.clear();
    }

    @Scheduled(fixedRate = 10*60*1000)
    private void updateGroupChat() {
        groupChatService.updateGroupActivityInfo();
    }

}
