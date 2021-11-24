package com.robot.dao;

import com.robot.pojo.ConstellationInfo;

/**
 * @author Michael
 */
public interface ConstellationInfoDao {
    /**
     * 增
     * @param constellationInfo
     * */
    void add(ConstellationInfo constellationInfo);

    /**
     * 查
     * @param constellationName 星座名
     * @param dateTime 当前日期 yyyy-MM-dd
     * @return
     * */
    ConstellationInfo queryToday(String dateTime,String constellationName);

    /**
     * 查count
     * @param constellationName 星座名
     * @param dateTime 当前日期 yyyy-MM-dd
     * @return
     * */
    int checkCountToday(String dateTime,String constellationName);
}
