package com.robot.service;

import com.robot.enums.GameType;
import com.robot.enums.Mode;

public interface ScheduleService {
    /**
     * 获取打工图片
     * @param content
     * @param nextTime
     * @return 图片地址
     * */
    String getSalmonRunSchedule(String content, int nextTime);

    /**
     * 获取对战图片
     * @param content
     * @param nextTime
     * @return 图片地址
     * */
    String getBattleSchedule(String content, int nextTime);

    /**
     * 获取单排/组排 模式对战图片
     * @param gameType 单排/组排
     * @param mode 模式
     * @param nextTime
     * @param timeZone 时差
     * @return 图片地址
     * */
    String getTypeModeSchedule(GameType gameType, Mode mode,int nextTime,int timeZone);
}
