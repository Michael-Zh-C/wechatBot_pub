package com.robot.dao;

import com.robot.pojo.LotteryInfo;

import java.util.List;

/**
 * @author zhang
 */
public interface LotteryInfoDao {
    /**
     * 根据ID查询抽签信息
     * @param id
     * @return
     * */
    LotteryInfo queryLotteryInfoById(int id);

    /**
     * 获取全部签文ID
     * @return
     * */
    List<Integer> getIds();
}
