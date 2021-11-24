package com.robot.service.impl;

import com.robot.common.EnumHelperUtil;
import com.robot.convertimg.BattlePdfUtils;
import com.robot.convertimg.SalmonRunPdfUtils;
import com.robot.dao.ScheduleBattleDao;
import com.robot.dao.ScheduleSalmonRunDao;
import com.robot.enums.GameType;
import com.robot.enums.Mode;
import com.robot.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Michael
 */
@Service
public class ScheduleServiceImpl implements ScheduleService {
    @Autowired
    private ScheduleSalmonRunDao scheduleSalmonRunDao;
    @Autowired
    private ScheduleBattleDao scheduleBattleDao;
    @Override
    public String getSalmonRunSchedule(String content, int nextTime) {
        content = content.trim();
        int timeZone = 0;

        if (content.length() > 1){
            if ((content.charAt(1) == '+' || content.charAt(1) == '-')){
                //时差处理
                try {
                    timeZone = Integer.parseInt(content.substring(1));
                } catch (Exception e) {
                    return "工后面只能加时差，比如'!工+4'就是查询比北京时间快4小时的当前工。别乱加参数，不然我会生气的哦";
                }
            } else {
                return "工后面只能加时差，比如'!工+4'就是查询比北京时间快4小时的当前工。别乱加参数，不然我会生气的哦";
            }
        }
        //10位时间戳
        long currentTime = System.currentTimeMillis()/1000;
        List<Map<String,Object>> workDetails = scheduleSalmonRunDao.getSalmonRunSchedule(currentTime,nextTime);

        Map<String ,Map<String,String>> maps = new HashMap<>();

        for (int i = 0 ; i < workDetails.size() ; i++) {
            Map<String,Object> workDetail = workDetails.get(i);
            //处理时间
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd hh:mm aa", Locale.ENGLISH);

            String startTime = sdf.format(getCurrentTimeZoneTime(new Date((Long) workDetail.get("startTime")*1000),timeZone));
            String endTime =  sdf.format(getCurrentTimeZoneTime(new Date((Long) workDetail.get("endTime")*1000),timeZone));
            String time = startTime + " - " + endTime;

            String mapImg = (String) workDetail.get("mapImg");
            String w1Weapon = (String) workDetail.get("w1Img");
            String w2Weapon = (String) workDetail.get("w2Img");
            String w3Weapon = (String) workDetail.get("w3Img");
            String w4Weapon = (String) workDetail.get("w4Img");
            Map<String,String> map = new HashMap<>();
            map.put("map",mapImg);
            map.put("time",time);
            map.put("weapon1",w1Weapon);
            map.put("weapon2",w2Weapon);
            map.put("weapon3",w3Weapon);
            map.put("weapon4",w4Weapon);
            maps.put("map" + i , map);
        }

        return SalmonRunPdfUtils.getSalmonRunImg(maps.get("map0"),maps.get("map1"));
    }

    @Override
    public String getBattleSchedule(String content, int nextTime) {
        content = content.trim();
        int timeZone = 0;

        if (content.length() > 1){
            if ((content.charAt(1) == '+' || content.charAt(1) == '-')){
                //时差处理
                try {
                    timeZone = Integer.parseInt(content.substring(1));
                } catch (Exception e) {
                    return "图后面只能加时差，比如'!图+4'就是查询比北京时间快4小时的当前地图。别乱加参数，不然我会生气的哦";
                }
            } else {
                return "图后面只能加时差，比如'!图+4'就是查询比北京时间快4小时的当前地图。别乱加参数，不然我会生气的哦";
            }
        }
        //10位时间戳
        long currentTime = System.currentTimeMillis()/1000;
        List<Map<String,Object>> battleDetails = scheduleBattleDao.getBattleSchedule(currentTime,nextTime * 3);
        Map<String,Map<String,String>> generateMap = new HashMap<>();
        String time = "";

        for (Map<String,Object> map : battleDetails) {
            GameType gameType = EnumHelperUtil.getByIntegerTypeCode(GameType.class,"getI", (Integer) map.get("type"));
            Mode mode = EnumHelperUtil.getByIntegerTypeCode(Mode.class,"getI", (Integer) map.get("gameMode"));
            Map<String,String> map1 = new HashMap<>();
            map1.put("mode",mode.getChineseMode().substring(0,2));
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

            time = sdf.format(getCurrentTimeZoneTime(new Date((long)map.get("startTime") * 1000),timeZone)) +
                    " - " + sdf.format(getCurrentTimeZoneTime(new Date((long)map.get("endTime") * 1000),timeZone));
            map1.put("map1", (String) map.get("map1Pic"));
            map1.put("map2", (String) map.get("map2Pic"));

            generateMap.put(gameType.toString(),map1);
        }
        String result = BattlePdfUtils.getBattleImg(generateMap.get(GameType.REGULAR_BATTLE.toString()),
                generateMap.get(GameType.RANKED_BATTLE.toString()),
                generateMap.get(GameType.LEAGUE_BATTLE.toString()),time);
        return result;
    }

    @Override
    public String getTypeModeSchedule(GameType gameType, Mode mode, int nextTime,int timeZone) {
        long currentTime = System.currentTimeMillis()/1000;

        long battleStartTime = scheduleBattleDao.getTypeModeBattleTime(currentTime,gameType.getI(),mode.getI(),nextTime);

        if (battleStartTime <= 0) {
            return "查的太远了，老任没给我这条数据";
        }

        List<Map<String,Object>> battleDetails = scheduleBattleDao.getBattleScheduleByCurrentTime(battleStartTime);
        Map<String,Map<String,String>> generateMap = new HashMap<>();
        String time = "";

        for (Map<String,Object> map : battleDetails) {
            GameType gameTypeForPic = EnumHelperUtil.getByIntegerTypeCode(GameType.class,"getI", (Integer) map.get("type"));
            Mode modeForPic = EnumHelperUtil.getByIntegerTypeCode(Mode.class,"getI", (Integer) map.get("gameMode"));
            Map<String,String> map1 = new HashMap<>();
            map1.put("mode",modeForPic.getChineseMode().substring(0,2));
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

            time = sdf.format(getCurrentTimeZoneTime(new Date((long)map.get("startTime") * 1000),timeZone)) +
                    " - " + sdf.format(getCurrentTimeZoneTime(new Date((long)map.get("endTime") * 1000),timeZone));
            map1.put("map1", (String) map.get("map1Pic"));
            map1.put("map2", (String) map.get("map2Pic"));

            generateMap.put(gameTypeForPic.toString(),map1);
        }
        String result = BattlePdfUtils.getBattleImg(generateMap.get(GameType.REGULAR_BATTLE.toString()),
                generateMap.get(GameType.RANKED_BATTLE.toString()),
                generateMap.get(GameType.LEAGUE_BATTLE.toString()),time);
        return result;
    }


    private Date getCurrentTimeZoneTime(Date currentTime,int timeZone) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentTime);
        calendar.add(Calendar.HOUR,timeZone);
        return calendar.getTime();
    }

}
