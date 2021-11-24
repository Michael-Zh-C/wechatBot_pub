package com.robot.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.robot.dao.ConstellationInfoDao;
import com.robot.enums.Constellation;
import com.robot.pojo.ConstellationInfo;
import com.robot.pool.HttpClientResult;
import com.robot.pool.HttpClientUtils;
import com.robot.service.ConstellationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Michael
 */
@Service
public class ConstellationServiceImpl implements ConstellationService {
    @Autowired
    private ConstellationInfoDao constellationInfoDao;

    @Override
    public String getConstellationReply(String constellationName) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String time = sdf.format(date);

        //判断今天是否有星座信息，如果没有，先更新
        int count = constellationInfoDao.checkCountToday(time,constellationName);
        if (count <= 0) {
            //需要更新信息
            System.out.println("目前没有星座信息，开始更新");
            this.updateConstellationInfo();
        }

        ConstellationInfo constellationInfo = constellationInfoDao.queryToday(time,constellationName);

        StringBuffer sb = new StringBuffer();
        sb.append(constellationInfo.getConstellationName()).append("今日运势：").append("\n");
        sb.append("综合指数：").append(constellationInfo.getAllNum()).append("\n");
        sb.append("健康指数：").append(constellationInfo.getHealthNum()).append("\n");
        sb.append("爱情指数：").append(constellationInfo.getLoveNum()).append("\n");
        sb.append("财运指数：").append(constellationInfo.getMoneyNum()).append("\n");
        sb.append("工作指数：").append(constellationInfo.getWorkNum()).append("\n");
        sb.append("幸运数字：").append(constellationInfo.getLuckyNumber()).append("\n");
        sb.append("速配星座：").append(constellationInfo.getqFriend()).append("\n");
        sb.append("总结：").append(constellationInfo.getSummary()).append("\n");

        return sb.toString();
    }

    @Override
    public void updateConstellationInfo() {
        for (Constellation constellation:Constellation.values()) {
            System.out.println(constellation.getChineseName());
            Map<String,String> params = new HashMap<>();
            params.put("key","bd031e530248692988f6dc4173957ba6");
            params.put("consName",constellation.getChineseName());
            params.put("type","today");
            String url = "http://web.juhe.cn/constellation/getAll";
            HttpClientResult result = HttpClientUtils.doGet(url,params);
            System.out.println("result = " + result);

            JSONObject jsonObject = JSONObject.parseObject(result.getContent());
            ConstellationInfo constellationInfo = new ConstellationInfo();
            System.out.println("json = " + jsonObject);
            constellationInfo.setConstellationName(jsonObject.getString("name"));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
            Date date;
            try {
                date = sdf.parse(jsonObject.getString("datetime"));
            } catch (Exception e) {
                date = new Date();
            }
            constellationInfo.setCurrentDate(date.getTime());
            constellationInfo.setAllNum(jsonObject.getInteger("all"));
            constellationInfo.setColor(jsonObject.getString("color"));
            constellationInfo.setHealthNum(jsonObject.getInteger("health"));
            constellationInfo.setLoveNum(jsonObject.getInteger("love"));
            constellationInfo.setMoneyNum(jsonObject.getInteger("money"));
            constellationInfo.setLuckyNumber(jsonObject.getInteger("number"));
            constellationInfo.setqFriend(jsonObject.getString("QFriend"));
            constellationInfo.setSummary(jsonObject.getString("summary"));
            constellationInfo.setWorkNum(jsonObject.getInteger("work"));

            constellationInfoDao.add(constellationInfo);
        }
    }
}
