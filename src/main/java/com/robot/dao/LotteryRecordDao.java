package com.robot.dao;

import com.robot.pojo.LotteryRecord;

import java.util.Map;

/**
 * @author zhang
 */
public interface LotteryRecordDao {
    /**
     * 增
     * @param lotteryRecord
     * */
    void add(LotteryRecord lotteryRecord);

    /**
     * 查询今日所有信息，用以服务器重启，数据重新载入
     * @return 所有抽签信息
     * */
    Map<String,Integer> getTodayLotteryRecord();

    /**
     * 删除所有，清空今日记录（定时任务调用）
     * */
    void deleteAll();
}
