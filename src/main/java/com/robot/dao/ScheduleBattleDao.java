package com.robot.dao;

import java.util.List;
import java.util.Map;

/**
 * @author Michael
 */
public interface ScheduleBattleDao {
    /**
     * 获取对战全部信息
     * @param currentTime 当前时间戳（10位）
     * @param nextTime3
     * @return
     * */
    List<Map<String, Object>> getBattleSchedule(long currentTime, int nextTime3);

    /**
     * 获取指定游戏类型、模式时间
     * @param currentTime 当前时间戳（10位）
     * @param mode 游戏模式
     * @param gameType 单排or组排
     * @param nextTime
     * @return 指定时间戳（10位）
     * */
    long getTypeModeBattleTime(long currentTime,int gameType,int mode,int nextTime);

    /**
     * 根据起始时间获取对战全部信息
     * @param currentTime 当前时间戳（10位）
     * @return
     * */
    List<Map<String, Object>> getBattleScheduleByCurrentTime(long currentTime);
}
