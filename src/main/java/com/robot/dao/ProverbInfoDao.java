package com.robot.dao;

import com.robot.pojo.ProverbInfo;

import java.util.Map;

/**
 * @author Michael
 */
public interface ProverbInfoDao {
    /**
     * 查询全部
     * @return
     * */
    Map<String, ProverbInfo> selectAll();
}
