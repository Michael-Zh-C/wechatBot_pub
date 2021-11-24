package com.robot.dao;

import java.util.List;
import java.util.Map;

public interface ScheduleSalmonRunDao {
    /**
     * 获取打工全部信息
     * @param currentTime 当前时间戳（10位）
     * @param nextTime
     * @return
     * */
    List<Map<String, Object>> getSalmonRunSchedule(long currentTime, int nextTime);
}
