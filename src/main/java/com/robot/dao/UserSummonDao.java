package com.robot.dao;

import com.robot.pojo.UserSummon;

import java.util.List;
import java.util.Map;

/**
 * @author Michael
 */
public interface UserSummonDao {
    /**
     * 增
     * @param userSummon
     * */
    void add(UserSummon userSummon);
    /**
     * 改
     * @param userSummon
     * */
    void edit(UserSummon userSummon);

    /**
     * 查询召唤物数量
     * @param userSummon
     * @return
     * */
    int selectUserCount(UserSummon userSummon);

    /**
     * 删
     * @param userSummon
     * */
    void delete(UserSummon userSummon);
    /**
     * 获取全部自定义回复消息
     * @return 自定义消息合集
     * */
    Map<String,List<String>> selectAll();
}
