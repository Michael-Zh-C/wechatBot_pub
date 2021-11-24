package com.robot.dao;

import com.robot.pojo.GroupActivityInfo;

import java.util.List;
import java.util.Map;

/**
 * @author Michael
 */
public interface GroupActivityInfoDao {
    /**
     * 增
     * @param groupActivityInfo
     * */
    void add(GroupActivityInfo groupActivityInfo);

    /**
     * 查询今日所有
     * @param dateTime 今日时间，格式为yyyy-MM-dd
     * @return
     * */
    List<Map<String, Object>> queryTodayAll(String dateTime);
}
